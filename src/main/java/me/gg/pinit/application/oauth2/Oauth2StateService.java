package me.gg.pinit.application.oauth2;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;

@Service
public class Oauth2StateService {

    private static final String STATE_KEY_PREFIX = "oidc:state:";
    private static final Duration STATE_TTL = Duration.ofMinutes(5);

    private final StringRedisTemplate redisTemplate;
    private final SecureRandom secureRandom = new SecureRandom();

    public Oauth2StateService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String createAndStoreState(String sessionId) {
        String state = generateState();
        String key = buildKey(state);

        redisTemplate.opsForValue().set(key, sessionId, STATE_TTL);
        return state;
    }

    public void verifyAndConsumeState(String state, String currentSessionId) {
        if (state == null || state.isBlank()) {
            throw new IllegalStateException("Missing state parameter");
        }

        String key = buildKey(state);

        String storedSessionId = redisTemplate.opsForValue().get(key);

        if (storedSessionId != null) {
            redisTemplate.delete(key);
        }

        if (storedSessionId == null) {
            throw new IllegalStateException("Invalid or expired state");
        }

        if (!storedSessionId.equals(currentSessionId)) {
            throw new IllegalStateException("State does not belong to this session");
        }
    }

    private String generateState() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String buildKey(String state) {
        return STATE_KEY_PREFIX + state;
    }
}

