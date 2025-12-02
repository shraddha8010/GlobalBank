package main;

public class Validator {

    public static boolean isValidName(String name) {
        return name != null && name.trim().length() >= 3;
    }

    public static boolean isValidContact(String contact) {
        return contact != null && contact.matches("\\d{10}");
    }

    public static boolean isValidAddress(String address) {
        return address != null && address.trim().length() >= 5;
    }

    public static boolean isValidAmount(double amount) {
        return amount > 0;
    }

    public static void printError(String msg) {
        System.out.println("[ERROR] " + msg);
    }
}
