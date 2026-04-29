package com.example.Banking_Wallet_System_Project.Controller;

import com.example.Banking_Wallet_System_Project.Service.AiService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/advice")
    public ResponseEntity<String> getAdvice(HttpServletRequest request,
                                            @RequestParam String prompt) {
        // Extract authenticated user's email from the request attribute set by JwtFilter
        String email = (String) request.getAttribute("userEmail");
        if (email == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        if (prompt == null || prompt.isBlank()) {
            return ResponseEntity.badRequest().body("Please provide a question.");
        }

        // Pass the email along with the prompt to the service
        return ResponseEntity.ok(aiService.getFinancialAdvice(email, email));
    }
}