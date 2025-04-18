package com.fiap.tech.challenge.infrastructure;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

public interface ApiTest {

    SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor ADMIN_JWT =
            jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));

    SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor USER_JWT =
            jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"));

}
