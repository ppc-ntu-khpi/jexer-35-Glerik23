package com.mybank.tui;

import com.mybank.domain.Account;
import com.mybank.domain.Bank; // Використовуємо ваш Bank
import com.mybank.domain.Customer; // Використовуємо ваш Customer
import com.mybank.domain.CheckingAccount; // Потрібні для instanceof
import com.mybank.domain.SavingsAccount;  // Потрібні для instanceof

import jexer.TAction;
import jexer.TApplication;
import jexer.TField;
import jexer.TText;
import jexer.TWindow;
import jexer.event.TMenuEvent;
import jexer.menu.TMenu;

/**
 *
 * @author Alexander 'Taurus' Babich
 */
public class TUIdemo extends TApplication {

    private static final int ABOUT_APP = 2000;
    private static final int CUST_INFO = 2010;

    public static void main(String[] args) throws Exception {
        // --- Змінено для завдання "На п'ятірку": читаємо дані з файлу ---
        Bank.readBankDataFromFile("test.dat");
        // ----------------------------------------------------------------

        TUIdemo tdemo = new TUIdemo();
        (new Thread(tdemo)).start();
    }

    public TUIdemo() throws Exception {
        super(BackendType.SWING);

        addToolMenu();
        TMenu fileMenu = addMenu("&File");
        fileMenu.addItem(CUST_INFO, "&Customer Info");
        fileMenu.addDefaultItem(TMenu.MID_SHELL);
        fileMenu.addSeparator();
        fileMenu.addDefaultItem(TMenu.MID_EXIT);

        addWindowMenu();

        TMenu helpMenu = addMenu("&Help");
        helpMenu.addItem(ABOUT_APP, "&About...");

        setFocusFollowsMouse(true);
        ShowCustomerDetails();
    }

    @Override
    protected boolean onMenu(TMenuEvent menu) {
        if (menu.getId() == ABOUT_APP) {
            messageBox("About", "\t\t\t\t\t    Just a simple Jexer demo.\n\nCopyright \u00A9 2019 Alexander 'Taurus' Babich").show();
            return true;
        }
        if (menu.getId() == CUST_INFO) {
            ShowCustomerDetails();
            return true;
        }
        return super.onMenu(menu);
    }

    private void ShowCustomerDetails() {
        TWindow custWin = addWindow("Customer Window", 2, 1, 40, 12, TWindow.NOZOOMBOX);
        // Оновлено статус-бар для відображення актуального діапазону номерів
        custWin.newStatusBar("Enter valid customer number (0-" + (Bank.getNumberOfCustomers() - 1) + ") and press Show...");

        custWin.addLabel("Enter customer number: ", 2, 2);
        TField custNo = custWin.addField(26, 2, 3, false);
        TText details = custWin.addText("Owner Name: \nAccount Type: \nAccount Balance: ", 2, 4, 38, 8);

        custWin.addButton("&Show", 30, 2, new TAction() {
            @Override
            public void DO() {
                try {
                    int custNum = Integer.parseInt(custNo.getText());
                    // Використовуємо статичні методи Bank.getCustomer та Bank.getNumberOfCustomers
                    if (custNum >= 0 && custNum < Bank.getNumberOfCustomers()) {
                        Customer customer = Bank.getCustomer(custNum);
                        if (customer != null) { // Перевірка, чи знайдено клієнта
                            String ownerName = customer.getFirstName() + " " + customer.getLastName();
                            StringBuilder accountInfo = new StringBuilder();

                            // Використовуємо customer.getNumberOfAccounts()
                            if (customer.getNumberOfAccounts() > 0) {
                                Account account = customer.getAccount(0); // Відображаємо інформацію про ПЕРШИЙ рахунок клієнта
                                if (account != null) { // Перевірка, чи знайдено рахунок
                                    String accountType = account.getClass().getSimpleName().replace("Account", ""); // Checking, Savings
                                    double balance = account.getBalance();

                                    accountInfo.append("Account Type: ").append(accountType).append("\n");
                                    accountInfo.append(String.format("Account Balance: $%.2f", balance));

                                    if (account instanceof CheckingAccount) {
                                        CheckingAccount chkAcc = (CheckingAccount) account;
                                        if (chkAcc.getOverdraftAmount() > 0) {
                                            accountInfo.append(String.format("\nOverdraft Amount: $%.2f", chkAcc.getOverdraftAmount()));
                                        }
                                    } else if (account instanceof SavingsAccount) {
                                        SavingsAccount savAcc = (SavingsAccount) account;
                                        accountInfo.append(String.format("\nInterest Rate: %.2f%%", savAcc.getInterestRate() * 100));
                                    }
                                } else {
                                    accountInfo.append("Error: Account not found for this customer.");
                                }
                            } else {
                                accountInfo.append("No accounts found for this customer.");
                            }
                            details.setText("Owner Name: " + ownerName + " (id=" + custNum + ")\n" + accountInfo.toString());
                        } else {
                             messageBox("Error", "Customer not found!").show(); // Клієнт не знайдений
                        }
                    } else {
                        messageBox("Error", "Invalid customer number! Valid range: 0-" + (Bank.getNumberOfCustomers() - 1)).show();
                    }
                } catch (NumberFormatException e) {
                    messageBox("Error", "You must provide a valid number!").show();
                } catch (Exception e) {
                    messageBox("Error", "An unexpected error occurred: " + e.getMessage()).show();
                }
            }
        });
    }
}