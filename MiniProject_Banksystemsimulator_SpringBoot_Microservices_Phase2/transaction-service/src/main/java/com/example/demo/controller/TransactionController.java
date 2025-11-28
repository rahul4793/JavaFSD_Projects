package com.example.demo.controller;


import com.example.demo.dto.DepositRequest;
import com.example.demo.dto.TransferRequest;
import com.example.demo.model.Transaction;
import com.example.demo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<Transaction> deposit(@RequestBody DepositRequest request) {
        try {
            Transaction transaction = transactionService.deposit(request);
            // If the fallback executed, the status will be FAILED
            return new ResponseEntity<>(transaction, 
                transaction.getStatus().equals("SUCCESS") ? HttpStatus.CREATED : HttpStatus.ACCEPTED);
        } catch (Exception e) {
            // Should only be reached if an unhandled error occurs
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transfer(@RequestBody TransferRequest request) {
        try {
            Transaction transaction = transactionService.transfer(request);
            return new ResponseEntity<>(transaction, 
                transaction.getStatus().equals("SUCCESS") ? HttpStatus.CREATED : HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}