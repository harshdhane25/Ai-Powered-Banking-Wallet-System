package com.example.Banking_Wallet_System_Project.Controller;

import com.example.Banking_Wallet_System_Project.Entity.Transaction;
import com.example.Banking_Wallet_System_Project.Service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(HttpServletRequest request,
                                           @RequestParam String receiverEmail,
                                           @RequestParam Double amount) {
        try {
            String senderEmail = (String) request.getAttribute("userEmail");
            if (senderEmail == null) return ResponseEntity.status(401).body("Unauthorized");
            return ResponseEntity.ok(transactionService.transfer(senderEmail, receiverEmail, amount));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> history(HttpServletRequest request) {
        try {
            String email = (String) request.getAttribute("userEmail");
            if (email == null) return ResponseEntity.status(401).body("Unauthorized");
            List<Transaction> transactions = transactionService.getUserTransactions(email);
            return ResponseEntity.ok(transactions);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}