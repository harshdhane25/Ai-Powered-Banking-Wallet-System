# Ai-Powered-Banking-Wallet-System
A comprehensive Banking Wallet System built with Spring Boot, Spring Security (JWT), MySQL, and Spring AI. Features include secure money transfers, transaction history, email notifications, and an integrated AI financial assistant powered by OpenRouter.

# 🏦 AI-Powered Banking Wallet System

A full-stack Banking Wallet Web Application built with **Spring Boot 3**, **Spring Security (JWT)**, **MySQL**, and **Spring AI**. This project allows users to securely register, add funds, transfer money to other users, track transaction history, and get real-time, context-aware financial advice from an integrated AI assistant.

## ✨ Features

* **🔐 Secure Authentication:** User registration and login using encrypted passwords (Bcrypt) and JWT (JSON Web Tokens) for stateless session management.
* **💰 Digital Wallet:** Users can check their balance and add money to their digital wallet.
* **💸 Peer-to-Peer Transfers:** Securely send money to other registered users via their email address.
* **📜 Transaction History:** View a chronological list of all incoming and outgoing transactions.
* **📧 Email Notifications:** Automated email alerts for account registration and successful money transfers using `spring-boot-starter-mail`.
* **🤖 AI Financial Assistant:** Integrated with **OpenRouter (Tencent/Gemma)** via Spring AI. The AI reads the user's live database context (current balance and recent transactions) to provide personalized financial advice and saving tips.
* **🖥️ Frontend UI:** A clean, responsive HTML/CSS/JavaScript frontend integrated directly into the Spring Boot static resources.

## 🛠️ Tech Stack

* **Backend:** Java 17, Spring Boot 3.3.4
* **Security:** Spring Security, JWT (jjwt 0.9.1)
* **Database:** MySQL, Spring Data JPA, Hibernate
* **AI Integration:** Spring AI (`spring-ai-openai-spring-boot-starter`) connected to OpenRouter API.
* **Frontend:** Vanilla HTML5, CSS3, JavaScript (Fetch API)
* **Build Tool:** Maven

## 🚀 Getting Started

### Prerequisites
* **Java 17** or higher installed.
* **Maven** installed.
* **MySQL Server** running locally on port `3306`.
* An **OpenRouter API Key** (for the AI features).
* A **Gmail Account & App Password** (for sending emails).

### 1. Clone the Repository
```bash
git clone [https://github.com/harshdhane25/banking-wallet-system-with-ai.git](https://github.com/YOUR-USERNAME/banking-wallet-system-with-ai.git)
cd banking-wallet-system-with-ai
