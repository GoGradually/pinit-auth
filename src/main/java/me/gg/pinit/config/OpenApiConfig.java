package me.gg.pinit.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Pinit Auth API",
                version = "0.1.0",
                description = "Pinit 인증 서버의 로컬/소셜 로그인, 토큰 재발급 API 문서"
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "로컬 개발"),
                @Server(url = "https://auth.pinit.go-gradually.me", description = "배포 서버")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
