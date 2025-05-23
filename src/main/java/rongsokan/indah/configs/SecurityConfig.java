package rongsokan.indah.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import rongsokan.indah.constants.SecurityConstant;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AuthFailureHandlerConfig authFailureHandlerConfig;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
            // Authorize request
            .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
            .requestMatchers(SecurityConstant.permitAll()).permitAll()
            .anyRequest().authenticated())

            // Configure session management
            .sessionManagement(sessionManagement -> sessionManagement
            .maximumSessions(1).expiredUrl("/login?expired"))

            // Configure form login
            .formLogin(formLogin -> formLogin
            .defaultSuccessUrl("/dashboard", true)
            .failureHandler(authFailureHandlerConfig)
            .loginPage("/login")
            .permitAll())

            // Configure logout and build
            .logout(logout -> logout.permitAll())
            .build();
    }

    // Configure Password
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}