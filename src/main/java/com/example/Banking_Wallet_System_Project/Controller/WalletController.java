package com.example.Banking_Wallet_System_Project.Controller;

import com.example.Banking_Wallet_System_Project.Service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addMoney(HttpServletRequest request, @RequestParam Double amount) {
        try {
            String email = (String) request.getAttribute("userEmail");
            if (email == null) return ResponseEntity.status(401).body("Unauthorized");
            return ResponseEntity.ok(walletService.addMoney(email, amount));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(HttpServletRequest request) {
        try {
            String email = (String) request.getAttribute("userEmail");
            if (email == null) return ResponseEntity.status(401).body("Unauthorized");
            return ResponseEntity.ok(walletService.getBalance(email));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}