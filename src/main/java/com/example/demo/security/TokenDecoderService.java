package com.example.demo.security;

import com.example.demo.dto.User;

public interface TokenDecoderService {

    User decodeToken(String token);
}
