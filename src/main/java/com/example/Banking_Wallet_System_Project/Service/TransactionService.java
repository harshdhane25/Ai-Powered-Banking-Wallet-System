package com.example.Banking_Wallet_System_Project.Service;

import com.example.Banking_Wallet_System_Project.Entity.Transaction;
import com.example.Banking_Wallet_System_Project.Entity.User;
import com.example.Banking_Wallet_System_Project.Entity.Wallet;
import com.example.Banking_Wallet_System_Project.Repo.TransactionRepository;
import com.example.Banking_Wallet_System_Project.Repo.UserRepository;
import com.example.Banking_Wallet_System_Project.Repo.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepo;
    private final UserRepository userRepo;
    private final WalletRepository walletRepo;
    private final EmailService emailService;

    public TransactionService(TransactionRepository transactionRepo, UserRepository userRepo,
                               WalletRepository walletRepo, EmailService emailService) {
        this.transactionRepo = transactionRepo;
        this.userRepo = userRepo;
        this.walletRepo = walletRepo;
        this.emailService = emailService;
    }

    @Transactional
    public String transfer(String senderEmail, String receiverEmail, Double amount) {
        if (amount == null || amount <= 0) {
            throw new RuntimeException("Invalid amount");
        }
        if (senderEmail.equalsIgnoreCase(receiverEmail)) {
            throw new RuntimeException("Cannot transfer money to yourself");
        }

        User sender = userRepo.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepo.findByEmail(receiverEmail)
                .orElseThrow(() -> new RuntimeException("Receiver not found. Check the email address."));

        Wallet senderWallet = walletRepo.findByUser(sender)
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));
        Wallet receiverWallet = walletRepo.findByUser(receiver)
                .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

        if (senderWallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance. Available: Rs. " + senderWallet.getBalance());
        }

        // Update balances
        senderWallet.setBalance(senderWallet.getBalance() - amount);
        receiverWallet.setBalance(receiverWallet.getBalance() + amount);
        walletRepo.save(senderWallet);
        walletRepo.save(receiverWallet);

        // Save transaction record
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);
        transaction.setType("TRANSFER");
        transactionRepo.save(transaction);

        // Send notification emails (failures won't rollback the transaction)
        emailService.sendEmail(
                sender.getEmail(),
                "Transfer Alert - Banking Wallet",
                "Hi " + sender.getName() + ",\n\nYou have successfully sent Rs. " + amount +
                " to " + receiver.getEmail() + ".\n\nRemaining balance: Rs. " + senderWallet.getBalance()
        );
        emailService.sendEmail(
                receiver.getEmail(),
                "Money Received - Banking Wallet",
                "Hi " + receiver.getName() + ",\n\nYou have received Rs. " + amount +
                " from " + sender.getEmail() + ".\n\nNew balance: Rs. " + receiverWallet.getBalance()
        );

        return "Transfer Successful! Rs. " + amount + " sent to " + receiverEmail;
    }

    public List<Transaction> getUserTransactions(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return transactionRepo.findBySenderOrReceiver(user, user);
    }
}