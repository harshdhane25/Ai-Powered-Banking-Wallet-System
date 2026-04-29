package com.example.Banking_Wallet_System_Project.Repo;

import com.example.Banking_Wallet_System_Project.Entity.Transaction;
import com.example.Banking_Wallet_System_Project.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySender(User sender);
    List<Transaction> findByReceiver(User receiver);
    List<Transaction> findBySenderOrReceiver(User sender, User receiver);
}