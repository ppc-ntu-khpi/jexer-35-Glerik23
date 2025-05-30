package com.mybank.domain;

import java.util.ArrayList;
import java.util.List; // Змінено на List

/**
 *
 * @author taurus
 */
public class Customer {

    private final String firstName;
    private final String lastName;
    // Змінено на List<Account>
    private List<Account> accounts = new ArrayList<>();

    public Customer(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Account getAccount(int index) {
        // --- Додано перевірку, щоб уникнути IndexOutOfBoundsException ---
        if (index >= 0 && index < accounts.size()) {
            return accounts.get(index);
        }
        return null; // Повертаємо null, якщо індекс недійсний
        // -------------------------------------------------------------
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public int getNumberOfAccounts() {
        return accounts.size();
    }

}