package com.ps;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String transactions = "transactions.csv";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        createFileIfNotExists();
        homeScreen();
    }

    private static void createFileIfNotExists() {
        File file = new File(transactions);
        if (!file.exists()) {
            try {
                file.createNewFile();
                try (PrintWriter writer = new PrintWriter(new FileWriter(transactions))) {
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
                case "0":
                    System.out.println("Exiting application. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void addTransaction(boolean isDeposit) {
        System.out.println("\n=== " +(isDeposit ? "Add Deposit" : "Make a Payment") + "===");
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

        try (PrintWriter writer = new PrintWriter(new FileWriter(transactions, true))) {
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
        int command = 0;
        do {
            System.out.println("\n The ULTIMATE Ledger");
            System.out.println("1) All Entries");
            System.out.println("2) Deposits");
            System.out.println("3) Payments");
            System.out.println("4) Reports");
            System.out.println("5) Home");
            System.out.println("Please select am option: ");

            String choice = scanner.nextLine().trim().toUpperCase();

            switch (command) {
                case 1:
                    displayTransactions(null);
                    break;
                case 2:
                    displayTransactions("Deposits");
                    break;
                case 3:
                    displayTransactions("Payments");
                    break;
                case 4:
                    reportScreen();
                    break;
                case 0:
                    System.out.println("Going home");
                    break;
                default:
                    System.out.println("Incorrect command, read carefully and try again");
            }
        } while (command != 0);

    }

    private static void displayTransactions(String filter) {
        List<Transaction> transactionList = loadTransactions();

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        // Sort by date and time descending (newest first)
        transactions.sort(Comparator.comparing(Transaction::date)
                .thenComparing(Transaction::time)
                .reversed());

        System.out.println("\n=== TRANSACTIONS ===");
        System.out.printf("%-12s %-10s %-25s %-20s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println("---------------------------------------------------------------");
    }

    private static List<Transaction> loadTransactions() {
        List<Transaction> transactionsList = new ArrayList<>();
        File file = new File(transactions);

        if (!file.exists()) {
            return transactionsList; // return empty list if file doesn't exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip the header
                    continue;
                }
                String[] parts = line.split("\\|");
                if (parts.length == 5) {
                    try {
                        LocalDate date = LocalDate.parse(parts[0]);
                        LocalTime time = LocalTime.parse(parts[1]);
                        String description = parts[2];
                        String vendor = parts[3];
                        double amount = Double.parseDouble(parts[4]);

                        transactionsList.add(new Transaction(date, time, description, vendor, amount));
                    } catch (Exception e) {
                        System.out.println("Skipping invalid line: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading transactions: " + e.getMessage());
        }

        return transactionsList;
    }

    private static void reportScreen() {
        int command;
        do {
            System.out.println("\nReports:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back");
            System.out.print("What would you like to do? ");
            command = scanner.nextInt();
            scanner.nextLine();

            switch (command) {
                case 1:
                    filterByPeriod("monthToDate");
                    break;
                case 2:
                    filterByPeriod("previousMonth");
                    break;
                case 3:
                    filterByPeriod("yearToDate");
                    break;
                case 4:
                    filterByPeriod("previousYear");
                    break;
                case 5:
                    searchByVendor();
                    break;
                case 0:
                    System.out.println("Going back");
                    break;
                default:
                    System.out.println("Incorrect command, going back");
            }
        } while (command != 0);
    }

    private static void filterByPeriod(String periodType) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = null;
        LocalDate endDate = null;
        String periodName = "";

        switch (periodType) {
            case "monthToDate":
                startDate = now.withDayOfMonth(1);
                endDate = now;
                periodName = "Month to Date";
                break;
            case "previousMonth":
                startDate = now.minusMonths(1).withDayOfMonth(1);
                endDate = now.withDayOfMonth(1).minusDays(1);
                periodName = "Previous Month";
                break;
            case "yearToDate":
                startDate = now.withDayOfYear(1);
                endDate = now;
                periodName = "Year to Date";
                break;
            default:
                System.out.println("Invalid period type");
                return;
        }


    }


    private static void searchByVendor () {
                System.out.print("Enter vendor name to search: ");
                String vendorSearch = scanner.nextLine().toLowerCase();

                List<Transaction> transactions = CSVHandler.readTransactions();
                boolean found = false;

                for (Transaction t : transactions) {
                    if (t.vendor().toLowerCase().contains(vendorSearch)) {
                        System.out.println(t.toCSVLine());
                        found = true;
                    }
                }
                if (!found) {
                    System.out.println("No transactions found for this vendor: " + vendorSearch);
                }

            }

    private record Transaction(LocalDate date, LocalTime time, String description, String vendor, double amount) {

        public String toCSVLine() {
            return String.format("%s|%s|%s|%s|%.2f", date, time, description, vendor, amount);
        }
    }

    public static class CSVHandler {
        private static final String transactions = "transactions.csv";

        public static List<Transaction> readTransactions() {
            List<Transaction> transactions = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(String.valueOf(transactions)))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] fields = line.split("\\|");
                    if (fields.length == 5) {
                        transactions.add(new Transaction(
                                transactions.add(new Transaction(
                                        LocalDate.parse(fields[0]),
                                        LocalTime.parse(fields[1]),
                                        fields[2],
                                        fields[3],
                                        Double.parseDouble(fields[4])
                       ));

                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            }
            return transactions;
        }

        public static void writeTransaction(Transaction t) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(transactions, true))) {
                bw.write(t.toCSVLine());
                bw.newLine();
            } catch (IOException e) {
                System.out.println("Error writing file: ");
    }
}
    }
}
