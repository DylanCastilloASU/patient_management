package com.pm.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    //changing spring security setup
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //telling spring security to permit all requests since we already have a first line of defence our API Gateway
        //therefore all incoming requests will be coming internally therefore chances of malicious content are slim
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                //disabling cross site request forgery again due to requests coming from API Gateway
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
