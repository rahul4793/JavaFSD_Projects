package com.example.demo.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    private String transactionId;
    private String type; // DEPOSIT, WITHDRAWAL, TRANSFER
    private Double amount;
    private LocalDateTime timestamp = LocalDateTime.now();
    private String status; // SUCCESS, FAILED
    private String sourceAccount; // Null for DEPOSIT
    private String destinationAccount;
}
