package com.example.demo.config;

import com.example.demo.security.TokenAuthenticationConverter;
import com.example.demo.security.TokenUserExtractor;
import com.example.demo.security.UserDetailsReactiveAuthenticationManager;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {

    private final TokenUserExtractor tokenUserExtractor;
    private static final String[] WHITELIST_ENDPOINTS = {"/public/**"};

    public WebSecurityConfig(TokenUserExtractor tokenUserExtractor) {
        this.tokenUserExtractor = tokenUserExtractor;
    }

    @Bean
    public UserDetailsReactiveAuthenticationManager reactiveAuthenticationManager() {
        return new UserDetailsReactiveAuthenticationManager();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        // Disable login form
        http
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .logout().disable();

        // Custom security filter
        http
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS)
                .permitAll()

                .and()
                .authorizeExchange()
                .matchers(EndpointRequest.to("health", "info", "metrics"))
                .permitAll()

                .pathMatchers(WHITELIST_ENDPOINTS)
                .permitAll()

                .and()
                .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange()
                .pathMatchers("/**")
                .hasAnyAuthority("INTERNAL", "ADMIN")

                .anyExchange()
                .authenticated();

        return http.build();
    }

    private AuthenticationWebFilter authenticationWebFilter() {
        final AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(reactiveAuthenticationManager());
        authenticationWebFilter.setServerAuthenticationConverter(new TokenAuthenticationConverter(
                this.tokenUserExtractor));
        // Stateless: NoOpServerSecurityContextRepository
        authenticationWebFilter.setSecurityContextRepository(new WebSessionServerSecurityContextRepository());
        return authenticationWebFilter;
    }
}
