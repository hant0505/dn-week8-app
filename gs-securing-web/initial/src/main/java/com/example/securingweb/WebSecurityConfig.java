package com.example.securingweb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

@Configuration
public class WebSecurityConfig {

    // 1. Chuỗi filter ưu tiên cao: Actuator + Webhook POST (public, CSRF disabled)
    @Bean
    @Order(1)
    public SecurityFilterChain actuatorAndWebhookSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            // Match đúng các path cần public
            .securityMatcher(new OrRequestMatcher(
                new AntPathRequestMatcher("/actuator/**"),
                new AntPathRequestMatcher("/webhook", "POST")
            ))
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(csrf -> csrf.disable()); // POST từ Alertmanager không có CSRF token

        return http.build();
    }

    // 2. Chuỗi filter cho ứng dụng chính
    @Bean
    @Order(3)
    public SecurityFilterChain applicationSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/register").permitAll()
                .requestMatchers("/js/**", "/css/**", "/images/**").permitAll()
                .anyRequest().authenticated() // Các request khác yêu cầu xác thực
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .logout(logout -> logout.permitAll())
            .httpBasic(Customizer.withDefaults()); // HTTP Basic cho các request còn lại

        return http.build();
    }
}
