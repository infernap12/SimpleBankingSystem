package banking;

import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static HashMap<String, Account> fakeDb = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;
        while (true) {//menu
            System.out.println("""
                    1. Create an account
                    2. Log into account
                    0. Exit""");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {
                    Account account = createAccount();
                    fakeDb.put(account.cardNumber, account);
                    System.out.printf("""                      
                            %nYour card has been created
                            Your card number:
                            %s
                            Your card PIN:
                            %s%n%n""", account.cardNumber, account.pinCode);
                }
                case 2 -> {
                    Account account = getLogin();
                    if (account != null) {
                        System.out.println("\nYou have successfully logged in!\n");
                        openAccount(account);
                    }
                }
                case 0 -> System.exit(0);
            }
        }
    }

    private static void openAccount(Account account) {
        Scanner scanner = new Scanner(System.in);
        int choice;
        while (true) {
            System.out.println("""
                    1. Balance
                    2. Log out
                    0. Exit""");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> System.out.printf("Balance: %f%n", account.balance);
                case 2 -> {
                    System.out.println("\nYou have successfully logged out!\n");
                    return;
                }
                case 3 -> System.exit(0);
            }

        }
    }

    private static Account getLogin() {
        Scanner scanner = new Scanner(System.in);
        String inputCard;
        String inputPIN;
        System.out.println("\nEnter you card number:");
        inputCard = scanner.nextLine();
        System.out.println("Enter your PIN:");
        inputPIN = scanner.nextLine();
        if (inputCard.matches("\\d{16}") && inputPIN.matches("\\d{4}")) {
            Account account = fakeDb.get(inputCard);
            if (account.pinCode.equals(inputPIN)) {
                return account;
            }
        }
        return null;
    }

    // 16 digits long
    // first 6 are 400000
    // 9 digit customer number
    // last digit is checksum, stage 1 is random
    private static Account createAccount() {
        StringBuilder cardNumber = new StringBuilder("400000");
        StringBuilder pinCode = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 9; i++) {
            cardNumber.append(random.nextInt(10));
        }
        for (int i = 0; i < 4; i++) {
            pinCode.append(random.nextInt(10));
        }
        cardNumber.append(util.check(cardNumber.toString()));
        return new Account(cardNumber.toString(), pinCode.toString(), 0);
    }

    record Account(String cardNumber, String pinCode, double balance) {
    }
}

