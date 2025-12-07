package me.gg.pinit.infra.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public FanoutExchange memberFanout() {
        return new FanoutExchange("auth.member.fanout");
    }

    @Bean
    public DirectExchange memberDirect() {
        return new DirectExchange("auth.member.direct");
    }

    @Bean
    public TopicExchange memberTopic() {
        return new TopicExchange("auth.member.topic");
    }
}
