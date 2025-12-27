package me.gg.pinit.authenticate.config;

import jakarta.servlet.http.HttpServletResponse;
import me.gg.pinit.authenticate.filter.JwtAuthenticationFilter;
import me.gg.pinit.authenticate.provider.JwtAuthenticationProvider;
import me.gg.pinit.infrastructure.jwt.JwtTokenProvider;
import me.gg.pinit.infrastructure.jwt.RsaKeyProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Duration;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Value("${path.key.jwt.private}")
    private String privateKeyPath;
    @Value("${path.key.jwt.public}")
    private String publicKeyPath;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e.authenticationEntryPoint(
                        (request, response, ex) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing token")
                ))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health/liveness", "/actuator/health/readiness", "/login", "/signup", "/refresh", "/login/**", "/v3/**", "/swagger-ui/**", "/async-api/**").permitAll()
                        .anyRequest().authenticated()
                );

        http.logout(AbstractHttpConfigurer::disable);

        http.addFilterBefore(
                jwtAuthenticationFilter(authenticationManager),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        return new JwtAuthenticationFilter(authenticationManager);
    }

    @Bean
    public AuthenticationManager authenticationManager(JwtAuthenticationProvider jwtAuthenticationProvider) {
        return new ProviderManager(jwtAuthenticationProvider);
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider(JwtTokenProvider jwtTokenProvider) {
        return new JwtAuthenticationProvider(jwtTokenProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        PrivateKey privateKey = RsaKeyProvider.loadPrivateKey(privateKeyPath);
        PublicKey publicKey = RsaKeyProvider.loadPublicKey(publicKeyPath);
        return new JwtTokenProvider(privateKey, publicKey, "https://pinit.go-gradually.me", Duration.ofMinutes(5), Duration.ofDays(14));
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(@Value("${app.frontend-base-url}") String frontendBaseUrl) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(frontendBaseUrl));
        configuration.setAllowedMethods(List.of(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name()
        ));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
