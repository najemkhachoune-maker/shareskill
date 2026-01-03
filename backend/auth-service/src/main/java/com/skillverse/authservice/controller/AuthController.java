package com.skillverse.authservice.controller;

import com.skillverse.authservice.dto.*;
import com.skillverse.authservice.model.User;
import com.skillverse.authservice.service.AuthService;
import com.skillverse.authservice.util.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/health")
    public String health() {
        return "Auth Service is up and running";
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        System.out.println("Received registration request for email: " + request.getEmail());
        try {
            User user = authService.register(
                    request.getEmail(),
                    request.getUsername(),
                    request.getPassword(),
                    request.getFirstName(),
                    request.getLastName());
            System.out.println("User registered successfully: " + user.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDTO(user));
        } catch (Exception e) {
            System.err.println("Error during registration: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        Map<String, Object> result = authService.login(request.getEmail(), request.getPassword());

        AuthResponse response = AuthResponse.builder()
                .accessToken((String) result.get("accessToken"))
                .refreshToken((String) result.get("refreshToken"))
                .expiresIn(((Number) result.get("expiresIn")).longValue())
                .userId((java.util.UUID) result.get("userId"))
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        Map<String, Object> result = authService.refresh(refreshToken);

        AuthResponse response = AuthResponse.builder()
                .accessToken((String) result.get("accessToken"))
                .refreshToken((String) result.get("refreshToken"))
                .expiresIn(((Number) result.get("expiresIn")).longValue())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        authService.logout(actualToken);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate token for other microservices")
    public ResponseEntity<Map<String, Boolean>> validateToken(@RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        boolean isValid = authService.validateToken(actualToken);
        return ResponseEntity.ok(Map.of("valid", isValid));
    }
}
