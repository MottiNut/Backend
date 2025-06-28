package com.mottinut.crosscutting.config;

import com.mottinut.crosscutting.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Endpoints de autenticación - permitir acceso sin autenticación
                        .requestMatchers(HttpMethod.POST, "/api/bff/auth/register/patient").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/bff/auth/register/nutritionist").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/bff/auth/login").permitAll()

                        // Endpoints de verificación - NUEVOS PERMISOS AGREGADOS
                        .requestMatchers(HttpMethod.POST, "/api/bff/auth/verification/send").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/bff/auth/verification/verify").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/bff/auth/verification/resend").permitAll()

                        // Endpoints de recuperación de contraseña
                        .requestMatchers(HttpMethod.POST, "/api/bff/auth/password/reset-request").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/bff/auth/password/reset").permitAll()

                        // Endpoints de salud y desarrollo
                        .requestMatchers("/api/auth/health").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/actuator/health").permitAll()

                        // H2 Console para desarrollo
                        .requestMatchers("/h2-console/**").permitAll()

                        // Swagger UI
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()

                        // Todos los demás endpoints requieren autenticación
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Para H2 Console en desarrollo
        http.headers(headers -> headers.frameOptions().disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}