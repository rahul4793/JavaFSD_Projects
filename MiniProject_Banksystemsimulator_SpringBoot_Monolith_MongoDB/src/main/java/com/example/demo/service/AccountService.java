package com.example.demo.service;

import com.example.demo.exception.AccountNotFoundException;
import com.example.demo.exception.InsufficientBalanceException;
import com.example.demo.exception.InvalidAmountException;
import com.example.demo.model.Account;
import com.example.demo.model.Transaction;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository,
                          TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    private String generateAccountNumber(String holderName) {
        String sanitized = holderName.replaceAll("\\s+", "");
        String initials = sanitized.length() >= 3
                ? sanitized.substring(0, 3).toUpperCase()
                : sanitized.toUpperCase();
        String randomDigits = String.valueOf(new Random().nextInt(9000) + 1000);
        return initials + randomDigits;
    }

    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis();
    }

    public Account createAccount(Account account) {
        if (account.getHolderName() == null || account.getHolderName().isBlank()) {
            throw new IllegalArgumentException("Holder name cannot be empty");
        }
        account.setAccountNumber(generateAccountNumber(account.getHolderName()));
        account.setBalance(0.0);
        account.setStatus("ACTIVE");
        account.setCreatedAt(new Date());

        log.info("Creating account for holderName={}, generatedAccountNumber={}",
                account.getHolderName(), account.getAccountNumber());

        return accountRepository.save(account);
    }

    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
    }

    public Account updateAccount(String accountNumber, Account updatedAccount) {
        Account existing = getAccountByNumber(accountNumber);

        if (updatedAccount.getHolderName() != null && !updatedAccount.getHolderName().isBlank()) {
            existing.setHolderName(updatedAccount.getHolderName());
        }
        if (updatedAccount.getStatus() != null && !updatedAccount.getStatus().isBlank()) {
            existing.setStatus(updatedAccount.getStatus());
        }

        log.info("Updating account {}, newHolderName={}, newStatus={}",
                accountNumber, existing.getHolderName(), existing.getStatus());

        return accountRepository.save(existing);
    }

    public void deleteAccount(String accountNumber) {
        Account existing = getAccountByNumber(accountNumber);
        log.info("Deleting account {}", accountNumber);
        accountRepository.delete(existing);
    }

    @Transactional
    public Account deposit(String accountNumber, double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive");
        }

        Account account = getAccountByNumber(accountNumber);
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        Transaction txn = new Transaction();
        txn.setTransactionId(generateTransactionId());
        txn.setType("DEPOSIT");
        txn.setAmount(amount);
        txn.setTimestamp(new Date());
        txn.setStatus("SUCCESS");
        txn.setSourceAccount(accountNumber);
        transactionRepository.save(txn);

        log.info("Deposit: account={}, amount={}, newBalance={}",
                accountNumber, amount, account.getBalance());

        return account;
    }

    @Transactional
    public Account withdraw(String accountNumber, double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive");
        }

        Account account = getAccountByNumber(accountNumber);
        if (account.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient balance for withdrawal");
        }

        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        Transaction txn = new Transaction();
        txn.setTransactionId(generateTransactionId());
        txn.setType("WITHDRAW");
        txn.setAmount(amount);
        txn.setTimestamp(new Date());
        txn.setStatus("SUCCESS");
        txn.setSourceAccount(accountNumber);
        transactionRepository.save(txn);

        log.info("Withdraw: account={}, amount={}, newBalance={}",
                accountNumber, amount, account.getBalance());

        return account;
    }

    @Transactional
    public void transfer(String sourceAccountNumber, String destinationAccountNumber, double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Transfer amount must be positive");
        }
        if (sourceAccountNumber.equals(destinationAccountNumber)) {
            throw new IllegalArgumentException("Source and destination accounts must be different");
        }

        Account source = getAccountByNumber(sourceAccountNumber);
        Account destination = getAccountByNumber(destinationAccountNumber);

        if (source.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient balance for transfer");
        }

        source.setBalance(source.getBalance() - amount);
        destination.setBalance(destination.getBalance() + amount);

        accountRepository.save(source);
        accountRepository.save(destination);

        Transaction txn = new Transaction();
        txn.setTransactionId(generateTransactionId());
        txn.setType("TRANSFER");
        txn.setAmount(amount);
        txn.setTimestamp(new Date());
        txn.setStatus("SUCCESS");
        txn.setSourceAccount(sourceAccountNumber);
        txn.setDestinationAccount(destinationAccountNumber);
        transactionRepository.save(txn);

        log.info("Transfer: source={}, dest={}, amount={}, sourceBalance={}, destBalance={}",
                sourceAccountNumber, destinationAccountNumber, amount,
                source.getBalance(), destination.getBalance());
    }

    public List<Transaction> getTransactionsForAccount(String accountNumber) {
        return transactionRepository.findBySourceAccountOrDestinationAccount(accountNumber, accountNumber);
    }
}
