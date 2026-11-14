package com.sharesphere.controller;

import com.sharesphere.dto.request.LoginRequest;
import com.sharesphere.dto.request.RegisterRequest;
import com.sharesphere.dto.response.AuthResponse;
import com.sharesphere.dto.response.UserSummaryResponse;
import com.sharesphere.security.CustomUserDetails;
import com.sharesphere.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserSummaryResponse user = authService.getCurrentUser(userDetails.getId());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "user", user
        ));
    }
}
