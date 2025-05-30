package com.mybank.domain;

import java.util.ArrayList;
import java.util.List; // Змінено на List
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author taurus
 */
public class Bank {

    private static Bank myBank;
    private List<Customer> customers = new ArrayList<>(); // Змінено на List

    private Bank() {}

    static {
        myBank = new Bank();
    }

    public static Bank getBank() {
        return myBank;
    }

    public static void addCustomer(String f, String l) {
        myBank.customers.add(new Customer(f, l));
    }

    public static int getNumberOfCustomers() {
        return myBank.customers.size();
    }

    public static Customer getCustomer(int index) {
        // --- Додано перевірку, щоб уникнути IndexOutOfBoundsException ---
        if (index >= 0 && index < myBank.customers.size()) {
            return myBank.customers.get(index);
        }
        return null; // Повертаємо null, якщо індекс недійсний
        // -------------------------------------------------------------
    }

    // --- НОВИЙ МЕТОД ДЛЯ ЗАВДАННЯ "НА П'ЯТІРКУ" ---
    public static void readBankDataFromFile(String filename) {
        Bank bank = Bank.getBank(); // Отримуємо єдиний екземпляр банку
        Customer currentCustomer = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(" "); // trim() для видалення зайвих пробілів
                if (parts.length == 0 || parts[0].isEmpty()) continue; // Пропускаємо порожні рядки

                switch (parts[0].toUpperCase()) {
                    case "CUSTOMER":
                        if (parts.length >= 3) {
                            String firstName = parts[1];
                            String lastName = parts[2];
                            bank.addCustomer(firstName, lastName); // Використовуємо статичний метод
                            currentCustomer = bank.customers.get(bank.customers.size() - 1); // Отримуємо останнього доданого клієнта
                        } else {
                            Logger.getLogger(Bank.class.getName()).log(Level.WARNING, "Invalid CUSTOMER line format: " + line);
                        }
                        break;
                    case "ACCOUNT":
                        if (currentCustomer != null && parts.length >= 3) {
                            String accountType = parts[1].toUpperCase();
                            try {
                                double balance = Double.parseDouble(parts[2]);
                                Account account = null;

                                switch (accountType) {
                                    case "CHECKING":
                                        if (parts.length >= 4) {
                                            double overdraftProtection = Double.parseDouble(parts[3]);
                                            account = new CheckingAccount(balance, overdraftProtection);
                                        } else {
                                            account = new CheckingAccount(balance); // Без овердрафту
                                        }
                                        break;
                                    case "SAVINGS":
                                        if (parts.length >= 4) {
                                            double interestRate = Double.parseDouble(parts[3]);
                                            account = new SavingsAccount(balance, interestRate);
                                        } else {
                                            account = new SavingsAccount(balance, 0.0); // За замовчуванням 0%
                                        }
                                        break;
                                    default:
                                        Logger.getLogger(Bank.class.getName()).log(Level.WARNING, "Unknown account type: " + accountType + " in line: " + line);
                                        break;
                                }
                                if (account != null) {
                                    currentCustomer.addAccount(account);
                                }
                            } catch (NumberFormatException e) {
                                Logger.getLogger(Bank.class.getName()).log(Level.SEVERE, "Error parsing number in ACCOUNT line: " + line, e);
                            }
                        } else if (currentCustomer == null) {
                            Logger.getLogger(Bank.class.getName()).log(Level.WARNING, "ACCOUNT defined before CUSTOMER in line: " + line);
                        } else {
                            Logger.getLogger(Bank.class.getName()).log(Level.WARNING, "Invalid ACCOUNT line format: " + line);
                        }
                        break;
                    default:
                        Logger.getLogger(Bank.class.getName()).log(Level.WARNING, "Unknown command in file: " + line);
                        break;
                }
            }
        } catch (IOException e) {
            Logger.getLogger(Bank.class.getName()).log(Level.SEVERE, "Error reading bank data from file: " + filename, e);
            System.err.println("Error reading bank data from file: " + e.getMessage());
        }
    }
    // --- КІНЕЦЬ НОВОГО МЕТОДУ ---
}