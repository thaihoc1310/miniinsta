package com.thaihoc.miniinsta.config;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(
                                Arrays.asList("http://localhost:3000", "http://localhost:4173",
                                                "http://localhost:5173", "http://localhost:8080/api-docs")); // Allowed
                // origins
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE",
                                "OPTIONS")); // Allowed methods
                configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type",
                                "Accept", "x-no-retry"));// Allowed headers
                configuration.setAllowCredentials(true);// Allow cookies
                configuration.setMaxAge(3600L);
                // How long the response from a pre-flight request can be cached by clients
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration); // Apply this configuration to all paths
                return source;
        }
}
