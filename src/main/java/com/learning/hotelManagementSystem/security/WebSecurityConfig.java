package com.learning.hotelManagementSystem.security;

import com.learning.hotelManagementSystem.types.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final JWTAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(httpSecurityConfigurer->httpSecurityConfigurer.disable())
                .sessionManagement(httpSecuritySessionManagementConfigurer->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy((SessionCreationPolicy.STATELESS)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll() // public URL are free to be accessed
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/admin/**").hasRole(UserType.ADMIN.name())
                        .requestMatchers("/customers/**").hasRole(UserType.CUSTOMER.name())
                        .requestMatchers("/bookings/**").hasAnyRole(UserType.ADMIN.name(),UserType.STAFF.name(),UserType.CUSTOMER.name())
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        ;
//        .formLogin(Customizer.withDefaults());
        return httpSecurity.build();
    }
}
