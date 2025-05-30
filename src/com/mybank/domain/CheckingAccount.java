package com.mybank.domain;

/**
 *
 * @author taurus
 */
public class CheckingAccount extends Account {

    private double overdraftAmount;

    public CheckingAccount(double balance,double overdraftAmount) {
        super(balance);
        this.overdraftAmount = overdraftAmount;
    }

    public CheckingAccount(double balance) {
        this(balance, 0);
    }

    // --- Додано геттер для використання в TUI ---
    public double getOverdraftAmount() {
        return overdraftAmount;
    }
    // -------------------------------------------------

    @Override
    public boolean withdraw(double amt) throws OverDraftAmountException {
        if ((balance + overdraftAmount)>amt) {
            balance = balance - amt;
            return true;
        } else {
            throw new OverDraftAmountException(amt-balance-overdraftAmount, "ERROR! Not sufficient funds!");
        }

    }

}