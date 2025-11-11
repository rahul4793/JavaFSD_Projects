package banksystemsimulator;

import banksystemsimulator.exceptions.BankingExceptions;

public class Account {
    private final String accountNumber;
    private final String holderName;
    private double balance;

    public Account(String holderName, String accountNumber) {
        this.holderName = holderName;
        this.accountNumber = accountNumber;
        this.balance = 0.0;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public synchronized double getBalance() {
        return balance;
    }

    public synchronized void deposit(double amount) throws BankingExceptions.InvalidAmountException {
        if (amount <= 0) {
            throw new BankingExceptions.InvalidAmountException("Deposit amount must be positive.");
        }
        balance += amount;
    }

    public synchronized void withdraw(double amount)
            throws BankingExceptions.InsufficientBalanceException, BankingExceptions.InvalidAmountException {
        if (amount <= 0) {
            throw new BankingExceptions.InvalidAmountException("Withdraw amount must be positive.");
        }
        if (amount > balance) {
            throw new BankingExceptions.InsufficientBalanceException("Insufficient balance.");
        }
        balance -= amount;
    }

    public static void transfer(Account from, Account to, double amount)
        throws BankingExceptions.InsufficientBalanceException, BankingExceptions.InvalidAmountException {
        if (amount <= 0) {
            throw new BankingExceptions.InvalidAmountException("Transfer amount must be positive.");
        }
        Account firstLock = from.getAccountNumber().compareTo(to.getAccountNumber()) < 0 ? from : to;
        Account secondLock = firstLock == from ? to : from;

        synchronized (firstLock) {
            synchronized (secondLock) {
                if (from.balance < amount) {
                    throw new BankingExceptions.InsufficientBalanceException("Insufficient balance in source account.");
                }
                from.balance -= amount;
                to.balance += amount;
            }
        }
    }

    @Override
    public String toString() {
        return String.format("Account Number: %s, Holder: %s, Balance: %.2f", accountNumber, holderName, balance);
    }
}
