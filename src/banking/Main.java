package banking;

import java.util.Scanner;

public class Main {
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
                    createAccount();
                    System.out.println("Your card has been created");
                    System.out.println("Your card number:");
                }
                case 2 -> login();
            }
        }
    }

    private static void login() {
    }

    private static account createAccount() {
        return new account();

    }

    record account(String accountNumber, String pinCode, double balance) {
    }
}

