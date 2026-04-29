package com.example.Banking_Wallet_System_Project.Service;

import com.example.Banking_Wallet_System_Project.Entity.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class AiService {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.chat.options.model}")
    private String model;

    private final RestTemplate restTemplate;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";

    // Inject RestTemplate alongside WalletService and TransactionService
    public AiService(RestTemplate restTemplate, WalletService walletService, TransactionService transactionService) {
        this.restTemplate = restTemplate;
        this.walletService = walletService;
        this.transactionService = transactionService;
    }

    public String getFinancialAdvice(String email, String prompt) {
        try {
            // 1. Fetch live contextual data for the authenticated user
            Double currentBalance = walletService.getBalance(email);
            List<Transaction> transactions = transactionService.getUserTransactions(email);

            // Format the last 5 transactions for the AI to read
            StringBuilder historyContext = new StringBuilder();
            int count = 0;
            for (int i = transactions.size() - 1; i >= 0; i--) { // Reverse loop for newest first
                if (count >= 5) break;
                Transaction tx = transactions.get(i);
                String counterparty = tx.getReceiver() != null ? tx.getReceiver().getEmail() : "Self";
                historyContext.append("- ").append(tx.getType())
                        .append(" of Rs.").append(tx.getAmount())
                        .append(" to/from ").append(counterparty)
                        .append(" on ").append(tx.getTimestamp().toLocalDate()).append("\n");
                count++;
            }

            if (historyContext.isEmpty()) {
                historyContext.append("No transaction history available yet.");
            }

            // 2. Build request headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            headers.set("HTTP-Referer", "http://localhost:8181");
            headers.set("X-Title", "Banking Wallet System");

            // 3. Build request body using Jackson ObjectNode
            ObjectNode body = objectMapper.createObjectNode();
            body.put("model", model);
            
            // INCREASED MAX TOKENS: Prevents reasoning models from cutting off early
            body.put("max_tokens", 1500); 

            ArrayNode messages = objectMapper.createArrayNode();

            // Create a dynamic System prompt containing the user's REAL data
            ObjectNode systemMsg = objectMapper.createObjectNode();
            systemMsg.put("role", "system");
            systemMsg.put("content",
                "You are a helpful, intelligent banking assistant. " +
                "Here is the user's real-time account data from the database:\n" +
                "Current Balance: Rs." + currentBalance + "\n" +
                "Recent Transactions:\n" + historyContext + "\n" +
                "Use this exact data to answer the user's query accurately. " +
                "If they ask for their balance or history, tell them based on the data above. " +
                "If they ask for saving tips, analyze their spending and give brief, practical advice. " +
                "Keep responses friendly, helpful, and concise."
            );
            messages.add(systemMsg);

            ObjectNode userMsg = objectMapper.createObjectNode();
            userMsg.put("role", "user");
            userMsg.put("content", prompt);
            messages.add(userMsg);

            body.set("messages", messages);

            String requestJson = objectMapper.writeValueAsString(body);

            // 4. Make the HTTP call
            HttpEntity<String> request = new HttpEntity<>(requestJson, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                OPENROUTER_URL, HttpMethod.POST, request, String.class
            );

            // 5. Parse response safely
            if (response.getBody() == null || response.getBody().isBlank()) {
                return "AI returned an empty response. Please try again.";
            }

            JsonNode root = objectMapper.readTree(response.getBody());

            if (root.has("error")) {
                return "AI Error: " + root.path("error").path("message").asText("Unknown API error");
            }

            JsonNode choices = root.path("choices");
            if (choices.isArray() && !choices.isEmpty()) {
                JsonNode messageNode = choices.get(0).path("message");
                String content = messageNode.path("content").asText(null);

                // Handle models that return "null" as string or actual null due to reasoning
                if (content == null || content.isBlank() || content.equals("null")) {
                    String reasoning = messageNode.path("reasoning").asText("");
                    if (!reasoning.isBlank()) {
                        return "AI Thought Process (Content was empty): " + reasoning.trim();
                    }
                } else {
                    return content.trim();
                }
            }

            return "AI returned an unexpected response format.";

        } catch (HttpClientErrorException e) {
            return "AI API Error (" + e.getStatusCode() + "): " + e.getResponseBodyAsString();
        } catch (HttpServerErrorException e) {
            return "AI service is temporarily down. Please try again later.";
        } catch (Exception e) {
            e.printStackTrace();
            return "AI error: " + e.getMessage();
        }
    }
}