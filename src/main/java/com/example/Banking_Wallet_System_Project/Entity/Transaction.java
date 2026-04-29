package com.example.Banking_Wallet_System_Project.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    private String type; // TRANSFER / ADD

    private LocalDateTime timestamp;

    // FIX: @JsonIgnoreProperties prevents infinite recursion:
    // Transaction → sender(User) → (no back-reference needed)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    private User sender;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    private User receiver;

    @PrePersist
    public void setTime() {
        this.timestamp = LocalDateTime.now();
    }
}