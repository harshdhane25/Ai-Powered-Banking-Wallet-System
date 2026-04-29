package com.example.Banking_Wallet_System_Project.Service;

import com.example.Banking_Wallet_System_Project.Config.JwtUtils;
import com.example.Banking_Wallet_System_Project.DTO.AuthRequest;
import com.example.Banking_Wallet_System_Project.DTO.AuthResponse;
import com.example.Banking_Wallet_System_Project.Entity.User;
import com.example.Banking_Wallet_System_Project.Entity.Wallet;
import com.example.Banking_Wallet_System_Project.Repo.UserRepository;
import com.example.Banking_Wallet_System_Project.Repo.WalletRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final WalletRepository walletRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;

    public AuthService(UserRepository userRepo, WalletRepository walletRepo,
                       PasswordEncoder passwordEncoder, JwtUtils jwtUtils,
                       EmailService emailService) {
        this.userRepo = userRepo;
        this.walletRepo = walletRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
    }

    public String register(User user) {
        // FIX: Proper validation checks
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new RuntimeException("Email is required");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new RuntimeException("Password is required");
        }
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepo.save(user);

        // Create Wallet for new user
        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setBalance(0.0);
        walletRepo.save(wallet);

        // Send welcome email (non-blocking — failure won't crash registration)
        emailService.sendEmail(
                savedUser.getEmail(),
                "Welcome to Banking Wallet",
                "Hello " + savedUser.getName() + ",\n\nYour account and wallet have been successfully created!\n\nYour starting balance is Rs. 0.00.\n\nThank you for joining Banking Wallet System."
        );

        return "User Registered Successfully";
    }

    public AuthResponse login(AuthRequest request) {
        // FIX: Better error message
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtils.generateToken(user.getEmail());
        return new AuthResponse(token);
    }
}