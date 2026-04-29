package com.example.Banking_Wallet_System_Project.Repo;

import com.example.Banking_Wallet_System_Project.Entity.User;
import com.example.Banking_Wallet_System_Project.Entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUser(User user);
}