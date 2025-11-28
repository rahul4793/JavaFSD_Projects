package com.example.demo.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "accounts")
public class Account {
    @Id
    private String id;
    @Indexed(unique = true)
    private String accountNumber;
    private String holderName;
    private Double balance;
    private String status = "ACTIVE";
}