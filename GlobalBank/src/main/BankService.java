package main;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Simple BankService for the Global Bank project — beginner friendly and plain
 * JDBC.
 * Methods match what your Main expects:
 * - registerAccount(Scanner)
 * - depositMoney(long, double)
 * - withdrawMoney(long, double, Scanner)
 * - checkBalance(long)
 * - printMiniStatement(long)
 */
public class BankService {

    /**
     * Register account and insert an ACCOUNT_OPEN transaction.
     */
    public void registerAccount(Scanner sc) {
        try {
            System.out.print("Enter full name: ");
            String name = sc.nextLine().trim();
            if (!Validator.isValidName(name))
                throw new IllegalArgumentException("Invalid name.");

            System.out.print("Enter contact (10 digits): ");
            String contact = sc.nextLine().trim();
            if (!Validator.isValidContact(contact))
                throw new IllegalArgumentException("Invalid contact.");

            System.out.print("Enter address: ");
            String address = sc.nextLine().trim();
            if (!Validator.isValidAddress(address))
                throw new IllegalArgumentException("Invalid address.");

            String insertSql = "INSERT INTO accounts (holder_name, contact, address, balance) VALUES (?, ?, ?, 0.0)";
            try (Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement pst = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

                pst.setString(1, name);
                pst.setString(2, contact);
                pst.setString(3, address);
                int r = pst.executeUpdate();
                if (r == 0) {
                    System.out.println("Account creation failed.");
                    return;
                }

                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        long accNo = rs.getLong(1);
                        insertTransactionSimple(conn, accNo, "ACCOUNT_OPEN", 0.0, 0.0);
                        System.out.println("Account created! Welcome to Global Bank. Your account number: " + accNo);
                    } else {
                        System.out.println("Account created but could not get account number.");
                    }
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("DB error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Deposit money — simple update and transaction insert.
     */
    public void depositMoney(long accNo, double amount) {
        if (!Validator.isValidAmount(amount)) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        String sel = "SELECT balance FROM accounts WHERE account_number = ?";
        String upd = "UPDATE accounts SET balance = ? WHERE account_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstSel = conn.prepareStatement(sel)) {

            pstSel.setLong(1, accNo);
            try (ResultSet rs = pstSel.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("Account not found.");
                }
                double balance = rs.getDouble("balance");
                double newBal = balance + amount;

                try (PreparedStatement pstUpd = conn.prepareStatement(upd)) {
                    pstUpd.setDouble(1, newBal);
                    pstUpd.setLong(2, accNo);
                    pstUpd.executeUpdate();
                }

                insertTransactionSimple(conn, accNo, "DEPOSIT", amount, newBal);
                System.out.printf("Deposited %.2f into account %d at Global Bank. New balance: %.2f%n", amount, accNo,
                        newBal);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("DB error on deposit: " + ex.getMessage(), ex);
        }
    }

    /**
     * Withdraw money — beginner behavior: allow only if sufficient balance.
     */
    public void withdrawMoney(long accNo, double amount, Scanner sc) {
        if (!Validator.isValidAmount(amount)) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }

        String sel = "SELECT balance FROM accounts WHERE account_number = ?";
        String upd = "UPDATE accounts SET balance = ? WHERE account_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstSel = conn.prepareStatement(sel)) {

            pstSel.setLong(1, accNo);
            try (ResultSet rs = pstSel.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("Account not found.");
                }
                double balance = rs.getDouble("balance");
                if (amount > balance) {
                    throw new IllegalArgumentException("Insufficient funds.");
                }

                double newBal = balance - amount;
                try (PreparedStatement pstUpd = conn.prepareStatement(upd)) {
                    pstUpd.setDouble(1, newBal);
                    pstUpd.setLong(2, accNo);
                    pstUpd.executeUpdate();
                }

                insertTransactionSimple(conn, accNo, "WITHDRAW", amount, newBal);
                System.out.printf("Withdrawal successful from Global Bank. New balance: %.2f%n", newBal);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("DB error on withdrawal: " + ex.getMessage(), ex);
        }
    }

    /**
     * Simple balance check.
     */
    public void checkBalance(long accNo) {
        String sel = "SELECT balance FROM accounts WHERE account_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sel)) {
            pst.setLong(1, accNo);
            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.next())
                    throw new IllegalArgumentException("Account not found.");
                double bal = rs.getDouble("balance");
                System.out.printf("Current balance for account %d at Global Bank: %.2f%n", accNo, bal);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("DB error on checkBalance: " + ex.getMessage(), ex);
        }
    }

    /**
     * Print last 10 transactions (most recent first). Simple formatting.
     */
    public void printMiniStatement(long accNo) {
        String sel = "SELECT type, amount, balance_after, created_at FROM transactions WHERE account_number = ? ORDER BY created_at DESC LIMIT 10";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sel)) {

            pst.setLong(1, accNo);
            try (ResultSet rs = pst.executeQuery()) {
                List<String> rows = new ArrayList<>();
                while (rs.next()) {
                    Timestamp t = rs.getTimestamp("created_at");
                    String type = rs.getString("type");
                    double amt = rs.getDouble("amount");
                    double bal = rs.getDouble("balance_after");
                    // simple sign rule
                    double signed = ("WITHDRAW".equals(type)) ? -Math.abs(amt) : Math.abs(amt);
                    rows.add(String.format("%s | %-10s | %+8.2f | bal: %8.2f", t.toString(), type, signed, bal));
                }
                if (rows.isEmpty()) {
                    System.out.println("No transactions yet.");
                } else {
                    System.out.println("Global Bank - Mini Statement (most recent first):");
                    for (String r : rows)
                        System.out.println(r);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("DB error on mini-statement: " + ex.getMessage(), ex);
        }
    }

    /* Helper: insert transaction row (very simple). */
    private void insertTransactionSimple(Connection conn, long accNo, String type, double amount, double balanceAfter) {
        String sql = "INSERT INTO transactions (account_number, type, amount, balance_after) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, accNo);
            pst.setString(2, type);
            pst.setDouble(3, amount);
            pst.setDouble(4, balanceAfter);
            pst.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("DB error inserting transaction: " + ex.getMessage(), ex);
        }
    }
}
