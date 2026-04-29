package com.example.Banking_Wallet_System_Project.Repo;

import com.example.Banking_Wallet_System_Project.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}