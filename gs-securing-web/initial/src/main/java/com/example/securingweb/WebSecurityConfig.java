package com.example.securingweb;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class WebSecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public WebSecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/", "/home", "/register", "/css/**", "/js/**").permitAll()
                // ⚡ Cho phép Alertmanager gọi webhook mà không cần login
                .requestMatchers("/webhook").permitAll()
                // Explicitly allow Prometheus scrape path (GET) so scrapers don't get redirected
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/actuator/prometheus").permitAll()
                // Keep actuator wildcard as a fallback for other actuator endpoints you want public
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN") // chỉ ADMIN mới vào
                .anyRequest().authenticated()
            )
            .httpBasic() // ✅ Allow HTTP Basic (useful for non-browser clients)
            .and()
            .formLogin(form -> form.loginPage("/login").permitAll())
            .logout((logout) -> logout.permitAll())
            // ⚠️ Tắt CSRF riêng cho webhook và prometheus scrape để POST/GET không bị 403
            .csrf(csrf -> csrf.ignoringRequestMatchers("/webhook", "/actuator/prometheus"));
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
