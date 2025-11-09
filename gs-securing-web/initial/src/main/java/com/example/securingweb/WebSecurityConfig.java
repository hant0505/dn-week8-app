package com.example.securingweb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.SecurityFilterChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class WebSecurityConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);
    
    // 1. Actuator endpoints (public, CSRF disabled)
    @Bean
    @Order(1)
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        logger.info("🔧 Configuring Actuator Security Filter Chain (Order 1)");
        http
            .securityMatcher("/actuator/**")
            .authorizeHttpRequests(auth -> {
                logger.info("✅ Actuator chain: permitAll for /actuator/**");
                auth.anyRequest().permitAll();
            })
            .csrf(csrf -> {
                logger.info("🔓 Actuator chain: CSRF DISABLED");
                csrf.disable();
            });
        return http.build();
    }
    
    // 2. Webhook endpoint (public, CSRF disabled)
    @Bean
    @Order(2)
    public SecurityFilterChain webhookSecurityFilterChain(HttpSecurity http) throws Exception {
        logger.info("🪝 Configuring Webhook Security Filter Chain (Order 2)");
        http
            .securityMatcher("/webhook/**")
            .authorizeHttpRequests(auth -> {
                logger.info("✅ Webhook chain: permitAll for /webhook/**");
                auth.anyRequest().permitAll();
            })
            .csrf(csrf -> {
                logger.info("🔓 Webhook chain: CSRF DISABLED");
                csrf.disable();
            });
        return http.build();
    }
    
    // 3. Application security (CSRF enabled)
    @Bean
    @Order(3)
    public SecurityFilterChain applicationSecurityFilterChain(HttpSecurity http) throws Exception {
        logger.info("🌐 Configuring Application Security Filter Chain (Order 3)");
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/register", "/js/**", "/css/**", "/images/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .logout(logout -> logout.permitAll())
            .httpBasic(Customizer.withDefaults());
        
        logger.info("🔒 Application chain: CSRF ENABLED (default)");
        return http.build();
    }
}
// package com.example.securingweb;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.annotation.Order;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.Customizer;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
// import org.springframework.security.web.util.matcher.OrRequestMatcher;

// @Configuration
// public class WebSecurityConfig {

//     // 1. Chuỗi filter ưu tiên cao: Actuator + Webhook POST (public, CSRF disabled)
//     @Bean
//     @Order(1)
//     public SecurityFilterChain actuatorAndWebhookSecurityFilterChain(HttpSecurity http) throws Exception {
//         http
//             // Match đúng các path cần public
//             .securityMatcher(new OrRequestMatcher(
//                 new AntPathRequestMatcher("/actuator/**"),
//                 new AntPathRequestMatcher("/webhook", "POST")
//             ))
//             .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
//             .csrf(csrf -> csrf.disable()); // POST từ Alertmanager không có CSRF token

//         return http.build();
//     }

//     // 2. Chuỗi filter cho ứng dụng chính
//     @Bean
//     @Order(3)
//     public SecurityFilterChain applicationSecurityFilterChain(HttpSecurity http) throws Exception {
//         http
//             .authorizeHttpRequests(auth -> auth
//                 .requestMatchers("/register").permitAll()
//                 .requestMatchers("/js/**", "/css/**", "/images/**").permitAll()
//                 .anyRequest().authenticated() // Các request khác yêu cầu xác thực
//             )
//             .formLogin(form -> form
//                 .loginPage("/login")
//                 .permitAll()
//             )
//             .logout(logout -> logout.permitAll())
//             .httpBasic(Customizer.withDefaults()); // HTTP Basic cho các request còn lại

//         return http.build();
//     }
// }
