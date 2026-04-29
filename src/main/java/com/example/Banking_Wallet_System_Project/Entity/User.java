package com.example.Banking_Wallet_System_Project.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
// FIX: Prevents infinite JSON recursion when User is nested in Transaction
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String mobile;

    // ✅ WRITE_ONLY: Jackson reads password FROM incoming requests (register),
    // but never writes it INTO outgoing JSON responses. 
    // @JsonIgnore was wrong — it blocked deserialization too, causing "Password is required" error.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}