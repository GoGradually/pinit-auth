package me.gg.pinit;

import me.gg.pinit.infrastructure.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
@SpringBootTest
class PinitAuthApplicationTests {

    @MockitoBean
    JwtTokenProvider jwtTokenProvider;


    @Test
    void contextLoads() {
    }
}
