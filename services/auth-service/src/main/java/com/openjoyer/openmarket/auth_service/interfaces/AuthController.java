package com.openjoyer.openmarket.auth_service.interfaces;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        return ResponseEntity.ok(Map.of("message", "ok"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register() {
        return null;
    }

    @PostMapping("/loing")
    public ResponseEntity<?> login() {
        return null;
    }
}
