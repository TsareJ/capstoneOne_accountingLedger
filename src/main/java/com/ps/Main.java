package com.ps;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static String transactionsCSV = "transactions.csv";
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        createFileIfNotExists();
        homeScreen();
    }

    private static void createFileIfNotExists() {
        File file = new File(transactionsCSV);
        if (!file.exists()) {
            try {
                file.createNewFile();
                try (PrintWriter writer = new PrintWriter(new FileWriter(transactionsCSV))) {
                    writer.println("date|time|description|vendor|amount");
                }
            } catch (IOException e) {
                System.out.println("File not found! Creating new transaction: ");
            }
        }
    }

    private static void homeScreen() {
        while (true) {
            System.out.println("\nWelcome Home: ");
            System.out.println("1) Add Deposit");
            System.out.println("2) Make Payment");
            System.out.println("3) Ledger");
            System.out.println("0) Exit");
            System.out.print("Make a selection");

            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "1":
                    addTransaction(true);
                    break;
                case "2":
                    addTransaction(false);
                    break;
                case "3":
                    ledgerScreen();
                    break;
                case "4":
                    System.out.println("Exiting application. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void addTransaction(boolean isDeposit) {
        System.out.println("\n=== " +(isDeposit ? "Add Deposit" : "Make a Payment" + "===");
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();

        System.out.print("Enter description: ");
        String description = scanner.nextLine().trim();

        System.out.print("Enter vendor: ");
        String vendor = scanner.nextLine().trim();

        double amount = 0;
        while (true) {
            System.out.print("Enter amount: ");
            try {
                amount = Double.parseDouble(scanner.nextLine().trim());
                if (amount <= 0) {
                    System.out.println("Amount must be positive. Please try again.");
                    continue;
                }
                if (!isDeposit) {
                    amount = -amount;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Please enter a valid number.");
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(transactionsCSV, true))) {
            writer.printf("%s|%s|%s|%s|%.2f%n",
                    date.format(dateTimeFormatter),
                    time.format(timeFormatter),
                    description,
                    vendor,
                    amount);
            System.out.println("This transaction has been added successfully!");
        } catch (IOException e) {
            System.out.println("Error saving transaction: ");
        }
    }

    private static void ledgerScreen() {
        while (true) {
            System.out.println("\n The ULTIMATE Ledger");
            System.out.println("1) All Entries");
            System.out.println("2) Deposits");
            System.out.println("3) Payments");
            System.out.println("4) Reports");
            System.out.println("5) Home");
            System.out.println("Please select am option: ");

            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "1":
                    displayTransactions(null);
                    break;
                case "2":
                    displayTransactions("Deposits");
                    break;
                case "3":
                    displayTransactions("Payments");

            }

        }

    }
}

