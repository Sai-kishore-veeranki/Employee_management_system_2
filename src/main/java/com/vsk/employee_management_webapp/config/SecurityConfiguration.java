package com.vsk.employee_management_webapp.config;

import com.vsk.employee_management_webapp.security.AuthEntryPointJwt;
import com.vsk.employee_management_webapp.security.AuthTokenFilter;
import com.vsk.employee_management_webapp.security.JwtUtils;
import com.vsk.employee_management_webapp.service.serviceInterface.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enable annotation-based security (e.g., @PreAuthorize)
public class SecurityConfiguration {

    private final UserService userService; // Your UserDetailsService
    private final AuthEntryPointJwt unauthorizedHandler; // For handling unauthenticated requests
    private final JwtUtils jwtUtils; // Your JWT utility

    public SecurityConfiguration(UserService userService, AuthEntryPointJwt unauthorizedHandler, JwtUtils jwtUtils) {
        this.userService = userService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtUtils = jwtUtils;
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(jwtUtils, userService);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(BCryptPasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless REST APIs
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // Handle unauthorized access
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Use stateless sessions
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // Allow auth endpoints (login, register)
                        .requestMatchers("/api/register/**").permitAll() // Keep register public
                        .anyRequest().authenticated() // All other requests require authentication
                )
        // .formLogin(Customizer.withDefaults()) // Remove formLogin
        // .httpBasic(Customizer.withDefaults()) // Remove httpBasic
        ;

        http.authenticationProvider(authenticationProvider(new BCryptPasswordEncoder())); // Use DAO authentication provider

        // Add our custom JWT filter before Spring Security's default UsernamePasswordAuthenticationFilter
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}