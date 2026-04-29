package com.example.Banking_Wallet_System_Project.DTO;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}