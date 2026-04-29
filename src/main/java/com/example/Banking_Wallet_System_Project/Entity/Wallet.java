package com.example.Banking_Wallet_System_Project.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
// FIX: Prevent Hibernate proxy serialization issues
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double balance = 0.0;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}