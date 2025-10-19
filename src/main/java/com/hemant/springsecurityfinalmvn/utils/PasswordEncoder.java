package com.hemant.springsecurityfinalmvn.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoder {
    @Bean
    public static org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}
