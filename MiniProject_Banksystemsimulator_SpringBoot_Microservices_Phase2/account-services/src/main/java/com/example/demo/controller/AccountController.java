package com.example.demo.controller;


import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        account.setBalance(Math.max(0.0, account.getBalance()));
        Account savedAccount = accountRepository.save(account);
        return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
    }
    
    @GetMapping("/{accountNumber}")
    public ResponseEntity<Account> getAccount(@PathVariable String accountNumber) {
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        return account.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                      .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    // Endpoint for Transaction Service to update balance
    @PutMapping("/{accountNumber}/balance")
    public ResponseEntity<?> updateBalance(@PathVariable String accountNumber, @RequestBody Double amount) {
        Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
        
        if (accountOpt.isEmpty()) {
            return new ResponseEntity<>("Account not found", HttpStatus.NOT_FOUND);
        }
        
        Account account = accountOpt.get();
        double newBalance = account.getBalance() + amount;
        
        // Simple validation for withdrawal
        if (newBalance < 0) {
            return new ResponseEntity<>("Insufficient funds", HttpStatus.BAD_REQUEST);
        }
        
        account.setBalance(newBalance);
        accountRepository.save(account);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }
    
    @PutMapping("/{accountNumber}/status")
    public ResponseEntity<Account> updateStatus(@PathVariable String accountNumber, @RequestBody String status) {
        Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
        if (accountOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Account account = accountOpt.get();
        account.setStatus(status.toUpperCase());
        accountRepository.save(account);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }
}