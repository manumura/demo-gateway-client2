package com.example.demo.security;

import com.example.demo.constant.Constant;
import com.example.demo.dto.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class UserDetailsReactiveAuthenticationManager implements ReactiveAuthenticationManager {

  @Override
  public Mono<Authentication> authenticate(final Authentication authentication) {
    if (authentication.isAuthenticated()) {
      return Mono.just(authentication);
    }

    return Mono.just(authentication)
        .switchIfEmpty(Mono.defer(this::raiseBadCredentialsException))
        .cast(UsernamePasswordAuthenticationToken.class)
        .flatMap(this::findUserDetails)
        .publishOn(Schedulers.parallel())
        .onErrorResume(e -> raiseBadCredentialsException())
        .map(u -> new UsernamePasswordAuthenticationToken(u, Constant.NA, u.getAuthorities()));
  }

  private <T> Mono<T> raiseBadCredentialsException() {
    return Mono.error(new BadCredentialsException("Invalid token"));
  }

  private Mono<UserDetails> findUserDetails(final UsernamePasswordAuthenticationToken authenticationToken) {
    log.info("checking authentication for username {}", authenticationToken.getName());
    Object principal = authenticationToken.getPrincipal();

    if (principal instanceof User) {
      return  Mono.justOrEmpty((User) principal);
    }

    return Mono.empty();
  }
}
