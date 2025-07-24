package com.choigoda.choigoda.dto;

import lombok.Getter;

@Getter
public class HomeResponseDto {
    private String email;
    private String name;
    private String message;

    public HomeResponseDto(String email, String name, String message) {
        this.email = email;
        this.name = name;
        this.message = message;
    }
}
