package com.thaihoc.miniinsta.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.thaihoc.miniinsta.dto.UserPrincipal;

import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfiguration {

    @Bean
    public AuditorAware<UUID> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() ||
                    authentication.getPrincipal().equals("anonymousUser")) {
                return Optional.empty();
            }

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return Optional.ofNullable(userPrincipal.getId());
        };
    }
}