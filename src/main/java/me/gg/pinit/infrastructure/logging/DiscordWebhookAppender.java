package me.gg.pinit.infrastructure.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.AppenderBase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class DiscordWebhookAppender extends AppenderBase<ILoggingEvent> {

    // logback-spring.xml 에서 주입
    @Setter
    private String webhookUrl;
    @Setter
    private String username = "pinit-auth-log";

    // Discord content 제한 (2000자)
    @Setter
    private int maxContentLength = 1900;

    @Setter
    private int connectTimeoutMillis = 2000;
    @Setter
    private int requestTimeoutMillis = 3000;

    private HttpClient client;
    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void start() {
        if (this.webhookUrl == null || this.webhookUrl.isEmpty()) {
            addWarn("디스코드 웹훅 URL이 설정되지 않았습니다. DiscordWebhookAppender가 시작되지 않습니다.");
            return;
        }
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(connectTimeoutMillis))
                .build();

        super.start();
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (!isStarted()) return;

        try {
            String content = format(event);
            content = truncate(content, maxContentLength);

            Map<String, Object> payload = Map.of(
                    "username", username,
                    "content", content,
                    "allowed_mentions", Map.of("parse", List.of())
            );

            String json = om.writeValueAsString(payload);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .timeout(Duration.ofMillis(requestTimeoutMillis))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() == 429) {
                long waitMs = parseRetryAfterMillis(resp);
                if (waitMs > 0)
                    Thread.sleep(waitMs);

                HttpResponse<String> retry = client.send(req, HttpResponse.BodyHandlers.ofString());
                if (retry.statusCode() >= 200 && retry.statusCode() < 300) {
                    return; // 성공
                }
                addWarn("DiscordWebhookAppender: 재시도 후에도 실패, 상태 코드: " + retry.statusCode());
                return;
            }
            if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                addWarn("DiscordWebhookAppender: HTTP 요청 실패, 상태 코드: " + resp.statusCode());
            }
        } catch (JsonProcessingException e) {
            addWarn("DiscordWebhookAppender: JSON 직렬화 실패", e);
        } catch (IOException e) {
            addWarn("DiscordWebhookAppender: HTTP 요청 실패", e);
        } catch (InterruptedException e) {
            addWarn("DiscordWebhookAppender: HTTP 요청이 중단됨", e);
        }
    }

    private String format(ILoggingEvent event) {
        String base = String.format(
                "[%s] %-5s %s - %s",
                java.time.Instant.ofEpochMilli(event.getTimeStamp()),
                event.getLevel(),
                event.getLoggerName(),
                event.getFormattedMessage()
        );

        if (event.getThrowableProxy() != null) {
            String stack = ThrowableProxyUtil.asString(event.getThrowableProxy());
            return base + "\n```" + stack + "```";
        }
        return base;
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, max) + "\n...(truncated)";
    }

    private long parseRetryAfterMillis(HttpResponse<String> resp) {
        String ra = resp.headers().firstValue("Retry-After").orElse(null);
        if (ra == null || ra.isBlank()) return 0;

        try {
            double v = Double.parseDouble(ra.trim());
            return (long) (v * 1000);
        } catch (NumberFormatException ignore) {
            return 0;
        }
    }
}
