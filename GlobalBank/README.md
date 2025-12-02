# 📘 Global Bank - Console Based Banking System

A simple **Java console** application connected to a **MySQL database**.  
This project allows users to create accounts, deposit money, withdraw money, check balance, and view a mini statement.

Designed to look like it was created by a **beginner Java developer** — simple, clean, and easy to understand.

## 📁 Project Structure

```
src/
 └─ main/
      ├─ Main.java
      ├─ BankService.java
      ├─ DatabaseConnection.java
      └─ Validator.java
```

## ⚙️ Technologies Used

- Java (Core Java + JDBC)
- MySQL Database
- MySQL Connector/J (JDBC Driver)

# 🚀 Features

### ✅ Create New Bank Account

User provides:

- Full Name
- Contact Number
- Address

Account is saved in the MySQL database with a generated account number.

### ✅ Deposit Money

Deposit any valid positive amount into an account.

### ✅ Withdraw Money

Withdraw money as long as the balance is sufficient.

### ✅ Check Balance

Fetch and display account balance.

### ✅ Mini Statement

Shows the latest 10 transactions.

# 🛢 MySQL Database Setup

### 1️⃣ Create Database

```sql
CREATE DATABASE IF NOT EXISTS globalbank;
```

### 2️⃣ Use Database

```sql
USE globalbank;
```

### 3️⃣ Create Accounts Table

```sql
CREATE TABLE IF NOT EXISTS accounts (
    account_number BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    holder_name VARCHAR(200) NOT NULL,
    contact VARCHAR(20),
    address VARCHAR(300),
    balance DOUBLE NOT NULL DEFAULT 0.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 4️⃣ Create Transactions Table

```sql
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    account_number BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    amount DOUBLE NOT NULL,
    balance_after DOUBLE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_number) REFERENCES accounts(account_number) ON DELETE CASCADE
);
```

# 🔗 Database Connection (DatabaseConnection.java)

```java
private static final String URL = "jdbc:mysql://localhost:3306/DatabseName";
private static final String USER = "root";
private static final String PASSWORD = "YourPassword";
```

# ▶️ Running the Project

### 1. Compile

```
dir lib
javac -encoding UTF-8 -cp "lib/mysql-connector-j-9.5.0.jar" -d bin src/main/*.java
```

### 2. Run

```
java -cp "bin;lib/mysql-connector-j-9.5.0.jar" main.Main
```
