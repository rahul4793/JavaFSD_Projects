package com.example.demo.service;


import com.example.demo.dto.DepositRequest;
import com.example.demo.dto.NotificationDto;
import com.example.demo.dto.TransferRequest;
import com.example.demo.feign.AccountServiceProxy;
import com.example.demo.feign.NotificationServiceProxy;
import com.example.demo.model.Transaction;
import com.example.demo.repository.TransactionRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private AccountServiceProxy accountServiceProxy;
    @Autowired
    private NotificationServiceProxy notificationServiceProxy;
    @Autowired
    private TransactionRepository transactionRepository;

    private static final String ACCOUNT_SERVICE_BREAKER = "accountServiceBreaker";

    @Transactional
    @CircuitBreaker(name = ACCOUNT_SERVICE_BREAKER, fallbackMethod = "handleAccountServiceFailure")
    public Transaction deposit(DepositRequest request) {
        
        // 1. Update balance in Account Service (Positive amount)
        accountServiceProxy.updateBalance(request.getAccountNumber(), request.getAmount()); 

        // 2. Save Transaction
        Transaction transaction = createTransaction(request.getAmount(), "DEPOSIT", "SUCCESS", null, request.getAccountNumber());
        Transaction savedTxn = transactionRepository.save(transaction);

        // 3. Send Notification
        notificationServiceProxy.sendNotification(new NotificationDto(
            request.getAccountNumber(), 
            String.format("Deposit of %.2f successful. TXN ID: %s", request.getAmount(), savedTxn.getTransactionId())
        ));

        return savedTxn;
    }
    
    // Fallback for Deposit
    public Transaction handleAccountServiceFailure(DepositRequest request, Throwable t) {
        System.err.println("🚨 CIRCUIT BREAKER OPEN/FAILURE: Deposit failed. " + t.getMessage());
        Transaction failedTxn = createTransaction(request.getAmount(), "DEPOSIT", "FAILED", null, request.getAccountNumber());
        return transactionRepository.save(failedTxn);
    }
    
    @Transactional
    @CircuitBreaker(name = ACCOUNT_SERVICE_BREAKER, fallbackMethod = "handleAccountServiceFailure")
    public Transaction transfer(TransferRequest request) {
        
        // 1. Withdraw from Source (Negative amount)
        accountServiceProxy.updateBalance(request.getSourceAccount(), -request.getAmount());
        
        // 2. Deposit to Destination (Positive amount)
        accountServiceProxy.updateBalance(request.getDestinationAccount(), request.getAmount()); 
        
        // 3. Save Transaction
        Transaction transaction = createTransaction(request.getAmount(), "TRANSFER", "SUCCESS", request.getSourceAccount(), request.getDestinationAccount());
        Transaction savedTxn = transactionRepository.save(transaction);

        // 4. Send Notification
        notificationServiceProxy.sendNotification(new NotificationDto(
            request.getSourceAccount(), 
            String.format("Transfer of %.2f to %s successful. TXN ID: %s", request.getAmount(), request.getDestinationAccount(), savedTxn.getTransactionId())
        ));

        return savedTxn;
    }
    
    // Fallback for Transfer (More complex, requires compensation or rollback if partial failure occurs)
    public Transaction handleAccountServiceFailure(TransferRequest request, Throwable t) {
        System.err.println("🚨 CIRCUIT BREAKER OPEN/FAILURE: Transfer failed. " + t.getMessage());
        Transaction failedTxn = createTransaction(request.getAmount(), "TRANSFER", "FAILED", request.getSourceAccount(), request.getDestinationAccount());
        return transactionRepository.save(failedTxn);
    }
    
    private Transaction createTransaction(Double amount, String type, String status, String source, String dest) {
        Transaction t = new Transaction();
        t.setTransactionId(UUID.randomUUID().toString());
        t.setType(type);
        t.setAmount(amount);
        t.setStatus(status);
        t.setSourceAccount(source);
        t.setDestinationAccount(dest);
        return t;
    }
}