package com.fiap.tech.challenge.infrastructure;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ActiveProfiles("test-integration")
@DataJpaTest
@ComponentScan(
        basePackages = "com.fiap.tech.challenge",
        useDefaultFilters = false,
        includeFilters = {
            @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*Gateway")
        }
)
@ExtendWith(PostgresSQLCleanUpExtension.class)
@Tag("integrationTest")
public @interface PostgresSQLGatewayTest {


}
