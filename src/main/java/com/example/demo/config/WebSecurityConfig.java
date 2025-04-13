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
    private static final String[] WHITELIST_ENDPOINTS = {"/", "/public/**"};

    public WebSecurityConfig(TokenUserExtractor tokenUserExtractor) {
        this.tokenUserExtractor = tokenUserExtractor;
    }

    @Bean
    public UserDetailsReactiveAuthenticationManager reactiveAuthenticationManager() {
        return new UserDetailsReactiveAuthenticationManager();
    }

    // https://stackoverflow.com/questions/78112501/sessionmanagement-and-csrf-cors-is-deprecated-since-version-6-1
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        // Disable login form
        http
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable);

        // Custom security filter
        http
                .authorizeExchange(authorize -> authorize
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .matchers(EndpointRequest.to("health", "info", "metrics")).permitAll()
                        .pathMatchers(WHITELIST_ENDPOINTS).permitAll()
                        .pathMatchers("/**").hasAnyAuthority("INTERNAL", "ADMIN", "SUPER-ADMIN")
                        .anyExchange().authenticated()
                )
                .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION);

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
