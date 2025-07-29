package com.choigoda.choigoda.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileController {

    @GetMapping("/api/profile")
    public ResponseEntity<Map<String, String>> getProfile(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity
                    .status(401)
                    .body(Map.of("error", "인증 필요"));
        }
        String username = auth.getName();
        return ResponseEntity.ok(
                Map.of(
                        "username", username,
                        "name", username  // 실제 User 엔티티의 필드를 쓰셔도 됩니다
                )
        );
    }
}

