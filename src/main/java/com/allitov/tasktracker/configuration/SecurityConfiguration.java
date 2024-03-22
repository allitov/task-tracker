package com.allitov.tasktracker.configuration;

import com.allitov.tasktracker.model.entity.RoleType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(ReactiveUserDetailsService userDetailsService,
                                                               PasswordEncoder passwordEncoder) {
        var reactiveAuthenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        reactiveAuthenticationManager.setPasswordEncoder(passwordEncoder);

        return reactiveAuthenticationManager;
    }

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity httpSecurity,
                                              ReactiveAuthenticationManager authenticationManager,
                                              ServerAuthenticationEntryPoint authenticationEntryPoint,
                                              ServerAccessDeniedHandler accessDeniedHandler) {
        httpSecurity
                .authorizeExchange(auth -> auth
                        .pathMatchers("/v3/api-docs/**", "/webjars/swagger-ui/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v2/user").permitAll()
                        .pathMatchers("/api/v2/user/**").hasAnyAuthority(
                                RoleType.USER.name(),
                                RoleType.MANAGER.name()
                        )
                        .pathMatchers(HttpMethod.POST, "/api/v2/task").hasAuthority(
                                RoleType.MANAGER.name()
                        )
                        .pathMatchers(HttpMethod.PUT, "/api/v2/task/{id}").hasAuthority(
                                RoleType.MANAGER.name()
                        )
                        .pathMatchers(HttpMethod.DELETE, "/api/v2/task/{id}").hasAuthority(
                                RoleType.MANAGER.name()
                        )
                        .pathMatchers("/api/v2/task/**").hasAnyAuthority(
                                RoleType.USER.name(),
                                RoleType.MANAGER.name()
                        )
                        .anyExchange().authenticated())
                .authenticationManager(authenticationManager)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(configurer -> configurer
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler));

        return httpSecurity.build();
    }
}
