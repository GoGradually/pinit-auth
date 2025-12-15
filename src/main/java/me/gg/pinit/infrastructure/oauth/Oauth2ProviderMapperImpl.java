package me.gg.pinit.infrastructure.oauth;

import me.gg.pinit.application.exception.ProviderNotFoundException;
import me.gg.pinit.application.oauth2.Oauth2ProviderMapper;
import me.gg.pinit.domain.oidc.Oauth2Provider;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Oauth2ProviderMapperImpl implements Oauth2ProviderMapper {
    private final Map<String, Oauth2Provider> providerMap = new ConcurrentHashMap<>();

    public Oauth2ProviderMapperImpl(ListableBeanFactory beanFactory) {
        beanFactory.getBeansWithAnnotation(Provider.class)
                .values()
                .forEach(this::register);
    }

    public Oauth2Provider get(String provider) {
        Oauth2Provider oauth2Provider = providerMap.get(provider);
        if (oauth2Provider == null) {
            throw new ProviderNotFoundException("지원하지 않는 provider 입니다. provider=" + provider);
        }
        return oauth2Provider;
    }

    private void register(Object bean) {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        if (!(bean instanceof Oauth2Provider oauth2Provider)) {
            throw new IllegalArgumentException("@Provider 는 Oauth2Provider 에만 붙일 수 있습니다. targetClass=" + targetClass.getName());
        }
        Provider providerAnnotation = AnnotatedElementUtils.findMergedAnnotation(targetClass, Provider.class);
        if (providerAnnotation == null) {
            return;
        }
        if (providerAnnotation.value().isBlank()) {
            throw new IllegalArgumentException("@Provider value는 비어있을 수 없습니다. targetClass=" + targetClass.getName());
        }
        Oauth2Provider duplicated = providerMap.putIfAbsent(providerAnnotation.value(), oauth2Provider);
        if (duplicated != null) {
            throw new IllegalStateException("중복된 provider 입니다. provider=" + providerAnnotation.value());
        }
    }
}
