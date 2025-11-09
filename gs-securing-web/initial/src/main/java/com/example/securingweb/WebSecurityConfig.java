package com.example.securingweb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

@Configuration
public class WebSecurityConfig {
    
    // Khai báo CustomUserDetailsService qua constructor nếu cần thiết cho authProvider
    // Nếu bạn không muốn sử dụng DaoAuthenticationProvider, bạn có thể comment lại phần này
    // private final CustomUserDetailsService userDetailsService;

    // public WebSecurityConfig(CustomUserDetailsService userDetailsService) {
    //      this.userDetailsService = userDetailsService;
    // }

    // 1. CHUỖI FILTER CHO ACTUATOR (Ưu tiên cao nhất)
    // Sẽ cho phép truy cập Actuator công khai, giải quyết lỗi 302

        // 1) Chain ưu tiên: Actuator + Webhook (public, CSRF disabled)
    @Bean
    @Order(1)
    public SecurityFilterChain actuatorAndWebhookSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            // match đúng các path cần public
            .securityMatcher(new OrRequestMatcher(
                new AntPathRequestMatcher("/actuator/**"),
                // bao cả /webhook và /webhook/* nếu bạn có subpaths
                new AntPathRequestMatcher("/webhook"),
                new AntPathRequestMatcher("/webhook/**")
            ))
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            // Alertmanager gửi POST không có CSRF token => disable CSRF cho các path này
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
    
    /*GIỮ */
    // @Bean
    // @Order(1)
    // public SecurityFilterChain actuatorAndWebhookSecurityFilterChain(HttpSecurity http) throws Exception {
    //     http
    //         .securityMatcher("/actuator/**") // hoặc "**" nếu muốn bao phủ nhiều path
    //         .authorizeHttpRequests(auth -> auth
    //             .anyRequest().permitAll()
    //         )
    //         .csrf(csrf -> csrf.disable());

    //     return http.build();
    // }
    /* */
    // public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
    //     // FIX LỖI: Không cần biến actuatorMatcher ở đây, dùng trực tiếp trong securityMatcher
    //     http
    //         // Dùng securityMatcher để chỉ định phạm vi của chuỗi filter này
    //         .securityMatcher(AntPathRequestMatcher.antMatcher("/actuator/**", "/webhook/**"))
    //         .authorizeHttpRequests(auth -> auth
    //             .anyRequest().permitAll() // FIX: Cho phép TẤT CẢ request trong phạm vi /actuator/**
    //     )
    //     .csrf(csrf -> csrf.disable());
    //     return http.build();
    // }
    
    // 2. CHUỖI FILTER CHO Ứng Dụng Chính
    @Bean
    @Order(3)
    public SecurityFilterChain applicationSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/register").permitAll()
                .requestMatchers("/webhook/**").permitAll() 
                .requestMatchers("/js/**", "/css/**", "/images/**").permitAll() 
                .anyRequest().authenticated() // Mọi request khác đều yêu cầu xác thực
            )
            .csrf(Customizer.withDefaults()) 
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .logout(logout -> logout
                .permitAll()
            )
            .httpBasic(Customizer.withDefaults()); 
            
        return http.build();
    }
    
    // 3. BEAN BẮT BUỘC CHO AUTHCONTROLLER VÀ XÁC THỰC
    
    // @Bean // <--- KHÔNG ĐƯỢC COMMENT: CẦN THIẾT CHO AuthController
    // public PasswordEncoder passwordEncoder() {
    //     return new BCryptPasswordEncoder();
    // }
    
    /* UNCOMMENT nếu bạn muốn cấu hình xác thực custom dựa trên DB */
    // @Bean
    // public DaoAuthenticationProvider authProvider(CustomUserDetailsService userDetailsService) {
    //     DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    //     authProvider.setUserDetailsService(userDetailsService);
    //     authProvider.setPasswordEncoder(passwordEncoder());
    //     return authProvider;
    // }
    
    // @Bean
    // public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    //      return config.getAuthenticationManager();
    // }
}

// package com.example.securingweb;


// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;

// @Configuration
// public class WebSecurityConfig {

//     private final CustomUserDetailsService userDetailsService;

//     public WebSecurityConfig(CustomUserDetailsService userDetailsService) {
//         this.userDetailsService = userDetailsService;
//     }

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//             .authorizeHttpRequests((requests) -> requests
//                 .requestMatchers("/", "/home", "/register", "/css/**", "/js/**").permitAll()
//                 // ⚡ Cho phép Alertmanager gọi webhook mà không cần login
//                 .requestMatchers("/webhook").permitAll()
//                             .requestMatchers("/actuator/prometheus").permitAll() // ✅ Bỏ HttpMethod.GET
//                 .requestMatchers("/actuator/**").permitAll()
//                 .requestMatchers("/admin/**").hasRole("ADMIN") // chỉ ADMIN mới vào
//                 .anyRequest().authenticated()
//             )
//             .httpBasic(httpBasic -> {}) // ✅ Dùng cú pháp mới
//             .formLogin(form -> form.loginPage("/login").permitAll())
//             .logout((logout) -> logout.permitAll())
//             // ⚠️ Tắt CSRF riêng cho webhook và prometheus scrape để POST/GET không bị 403
//             .csrf(csrf -> csrf.ignoringRequestMatchers("/webhook", "/actuator/prometheus"));
//         return http.build();
//     }

//     @Bean
//     public DaoAuthenticationProvider authProvider() {
//         DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//         authProvider.setUserDetailsService(userDetailsService);
//         authProvider.setPasswordEncoder(passwordEncoder());
//         return authProvider;
//     }

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     @Bean
//     public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//         return config.getAuthenticationManager();
//     }
// }
