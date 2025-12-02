package main;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner getinput = new Scanner(System.in);
        BankService bankService = new BankService();

        System.out.println("  WELCOME TO GLOBAL BANK CONSOLE  ");

        boolean running = true;

        while (running) {
            System.out.println("\n---- MAIN MENU ----");
            System.out.println("1  New Registration");
            System.out.println("2  Deposit Money");
            System.out.println("3  Withdraw Money");
            System.out.println("4  Check Balance");
            System.out.println("5  Mini Statement");
            System.out.println("6  Exit");
            System.out.print("Enter your choice: ");

            String raw = getinput.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Invalid input! Please enter a number between 1 and 6.");
                continue;
            }

            try {
                switch (choice) {
                    case 1:
                        bankService.registerAccount(getinput);
                        break;

                    case 2:
                        System.out.print("Enter Account Number: ");
                        long depAcc = Long.parseLong(getinput.nextLine().trim());
                        System.out.print("Enter Amount to Deposit: ");
                        double depAmt = Double.parseDouble(getinput.nextLine().trim());
                        bankService.depositMoney(depAcc, depAmt);
                        break;

                    case 3:
                        System.out.print("Enter Account Number: ");
                        long withAcc = Long.parseLong(getinput.nextLine().trim());
                        System.out.print("Enter Amount to Withdraw: ");
                        double withAmt = Double.parseDouble(getinput.nextLine().trim());
                        bankService.withdrawMoney(withAcc, withAmt, getinput);
                        break;

                    case 4:
                        System.out.print("Enter Account Number: ");
                        long balAcc = Long.parseLong(getinput.nextLine().trim());
                        bankService.checkBalance(balAcc);
                        break;

                    case 5:
                        System.out.print("Enter Account Number: ");
                        long stmtAcc = Long.parseLong(getinput.nextLine().trim());
                        bankService.printMiniStatement(stmtAcc);
                        break;

                    case 6:
                        System.out.println("Thank you for using Global Bank!");
                        running = false;
                        break;

                    default:
                        System.out.println("[ERROR] Invalid choice! Please select 1–6.");
                        break;
                }
            } catch (IllegalArgumentException iae) {
                System.out.println("[ERROR] " + iae.getMessage());
            } catch (Exception ex) {
                System.out.println("[ERROR] Operation failed: " + ex.getMessage());
            }
        }

        getinput.close();
    }
}
