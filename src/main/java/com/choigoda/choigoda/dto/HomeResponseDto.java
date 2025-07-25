package com.choigoda.choigoda.dto;

import lombok.Getter;

@Getter
public class HomeResponseDto {
    private String username;
    private String message;

    public HomeResponseDto(String email, String username, String message) {
        this.username = username;
        this.message = message;
    }
}
