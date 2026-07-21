package com.example.secondphone;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:context-test;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "app.jwt.secret=test-only-jwt-secret-at-least-32-characters"
})
class SecondPhonePlatformApplicationTests {

    @Test
    void contextLoads() {
    }
}
