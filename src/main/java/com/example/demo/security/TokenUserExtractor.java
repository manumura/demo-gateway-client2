package com.example.demo.security;

import com.example.demo.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@Component
public class TokenUserExtractor {

  private final TokenDecoderService tokenDecoderService;

  public TokenUserExtractor(TokenDecoderService tokenDecoderService) {
    this.tokenDecoderService = tokenDecoderService;
  }

  public Mono<Authentication> getAuthentication(String token) {
    if (StringUtils.isBlank(token)) {
      return Mono.error(new BadCredentialsException("Invalid token"));
    }

    Mono<User> userMono = Mono.justOrEmpty(tokenDecoderService.decodeToken(token));
    return userMono
        .onErrorResume(e -> {
          log.warn(e.getMessage());
          return Mono.error(new BadCredentialsException("Invalid token"));
        })
        .filter(Objects::nonNull)
        .map(user -> new UsernamePasswordAuthenticationToken(user, Constant.NA));
  }
}
