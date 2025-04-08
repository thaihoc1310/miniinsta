package com.thaihoc.miniinsta.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.thaihoc.miniinsta.service.OAuth2UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final OAuth2UserService oAuth2UserService;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http,
      CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
      OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) throws Exception {
    String[] whiteList = {
        "/", "/api/v1/auth/login", "/api/v1/auth/refresh", "api/v1/auth/register",
        "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"
    };
    log.warn("Configuring http filterChain");
    http
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(whiteList).permitAll()
            .anyRequest().authenticated())

        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(infoEndpoint -> infoEndpoint.userService(oAuth2UserService))
            .successHandler(oAuth2LoginSuccessHandler))

        .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults())
            .authenticationEntryPoint(customAuthenticationEntryPoint))

        .csrf(csrf -> csrf.disable())
        .cors(Customizer.withDefaults())
        .formLogin(formLogin -> formLogin.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }
}
