package com.fiap.tech.challenge.infrastructure;

import com.fiap.tech.challenge.infrastructure.configuration.WebServerConfig;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ActiveProfiles("test-integration")
@SpringBootTest(classes = WebServerConfig.class)
@ExtendWith(PostgresSQLCleanUpExtension.class)
@Tag("integrationTest")
public @interface IntegrationTest {
}
