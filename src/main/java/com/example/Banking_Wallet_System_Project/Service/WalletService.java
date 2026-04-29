package com.example.Banking_Wallet_System_Project.Service;

import com.example.Banking_Wallet_System_Project.Entity.User;
import com.example.Banking_Wallet_System_Project.Entity.Wallet;
import com.example.Banking_Wallet_System_Project.Repo.UserRepository;
import com.example.Banking_Wallet_System_Project.Repo.WalletRepository;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    private final WalletRepository walletRepo;
    private final UserRepository userRepo;

    public WalletService(WalletRepository walletRepo, UserRepository userRepo) {
        this.walletRepo = walletRepo;
        this.userRepo = userRepo;
    }

    public Double getBalance(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Wallet wallet = walletRepo.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        return wallet.getBalance();
    }

    public String addMoney(String email, Double amount) {
        if (amount == null || amount <= 0) {
            throw new RuntimeException("Amount must be greater than 0");
        }
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Wallet wallet = walletRepo.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance() + amount);
        walletRepo.save(wallet);
        return "Money added successfully. New balance: Rs. " + wallet.getBalance();
    }
}