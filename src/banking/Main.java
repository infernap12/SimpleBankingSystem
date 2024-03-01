package banking;

import org.sqlite.SQLiteDataSource;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class Main {
    static SQLiteDataSource dbDataSource;
    static Logger mylogger = Logger.getLogger(Main.class.getSimpleName());

    static {
        mylogger.setUseParentHandlers(false);
        FileHandler fh;
        try {
            fh = new FileHandler("test.log", true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mylogger.addHandler(fh);
    }

    public static void main(String[] args) {
        if (args.length == 2) {
            if (args[0].equals("-fileName")) {
                dbDataSource = dbUtils.dbInit(args[1]);
            }
        } else {
            System.out.println("Bad args");
            System.exit(1);
        }
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
                    System.out.printf("""

                            Your card has been created
                            Your card number:
                            %s
                            Your card PIN:
                            %s

                            """, account.cardNumber(), account.pinCode());
                }
                case 2 -> {
                    Account account = getLogin();
                    if (account != null) {
                        System.out.println("\nYou have successfully logged in!\n");
                        openAccount(account);
                    } else {
                        System.out.println("\n Wrong card number or PIN!");
                    }
                }
                case 0 -> {
                    System.out.println("\nBye!");
                    System.exit(0);
                }
            }
        }
    }

    public static void openAccount(Account account) {
        Scanner scanner = new Scanner(System.in);
        int choice;
        while (true) {
            System.out.println("""
                    
                    1. Balance
                    2. Add income
                    3. Do transfer
                    4. Close account
                    5. Log out
                    0. Exit""");
            choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1 -> System.out.printf("Balance: %d%n", account.balance());
                case 2 -> {
                    System.out.println("\nEnter income:");
                    int input = Integer.parseInt(scanner.nextLine());
                    account = dbUtils.addBalance(account, input);
                    System.out.println("Income was added!");
                }
                case 3 -> {

                    System.out.println("\nTransfer\nEnter card number:");
                    String cardNumber = scanner.nextLine();
                    if (!util.isValidCard(cardNumber)) {
                        System.out.println("Probably you made a mistake in the card number. Please try again!");
                        continue;
                    }
                    Account destAccount = dbUtils.getAccount(cardNumber);
                    if (destAccount == null) {
                        System.out.println("Such a card does not exist.");
                        continue;
                    }
                    System.out.println("Enter how much money you want to transfer:");
                    int input = Integer.parseInt(scanner.nextLine());
                    if (input > account.balance()) {
                        System.out.println("Not enough money!");
                        continue;
                    }
                    dbUtils.addBalance(destAccount, input);
                    account = dbUtils.addBalance(account, -input);
                    System.out.println("Success!");
                }

                case 4 -> {

                    System.out.println(dbUtils.deleteAccount(account) ? "\nThe account has been closed!\n" : "\nError account not closed\n");
                    return;
                }
                case 5 -> {
                    System.out.println("\nYou have successfully logged out!\n");
                    return;
                }
                case 0 -> {
                    System.out.println("\nBye!");
                    System.exit(0);
                }
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
            return dbUtils.getAccount(inputCard, inputPIN);
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
        mylogger.info(cardNumber.toString());
        cardNumber.append(util.luhnDigit(cardNumber.toString()));
        mylogger.info(cardNumber.toString());
        return dbUtils.addAccount(cardNumber.toString(), pinCode.toString());
//        System.out.println(util.isValidCard(cardNumber.toString()));
    }

}

