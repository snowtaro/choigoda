package com.choigoda.choigoda.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private final String token;
    public AuthResponse(String token) {
        this.token = token;
    }
}

