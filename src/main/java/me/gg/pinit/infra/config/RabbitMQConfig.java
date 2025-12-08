package me.gg.pinit.infra.config;

import me.gg.pinit.domain.event.DomainEventPublisher;
import me.gg.pinit.infra.events.AmqpEventMapper;
import me.gg.pinit.infra.events.MemberMessaging;
import me.gg.pinit.infra.events.RabbitDomainEventPublisher;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RabbitMQConfig {
    @Bean
    public DirectExchange memberDirect() {
        return new DirectExchange(MemberMessaging.DIRECT_EXCHANGE);
    }

    @Bean
    public DomainEventPublisher domainEventPublisher(RabbitTemplate rabbitTemplate,
                                                     List<AmqpEventMapper<?>> mapperList) {
        // mapperList에는 스프링이 관리하는 모든 AmqpEventMapper 구현체가 주입된다.
        // 이는 스프링 DI의 기능 중 하나이다.
        return new RabbitDomainEventPublisher(rabbitTemplate, mapperList);
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
