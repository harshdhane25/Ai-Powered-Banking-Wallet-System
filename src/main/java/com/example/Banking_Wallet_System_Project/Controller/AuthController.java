package com.example.Banking_Wallet_System_Project.Controller;

import com.example.Banking_Wallet_System_Project.DTO.AuthRequest;
import com.example.Banking_Wallet_System_Project.DTO.AuthResponse;
import com.example.Banking_Wallet_System_Project.Entity.User;
import com.example.Banking_Wallet_System_Project.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        try {
            String result = authService.register(user);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            // FIX: Return 400 with error message instead of 500
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // FIX: Return 401 with message instead of 500 crash
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}