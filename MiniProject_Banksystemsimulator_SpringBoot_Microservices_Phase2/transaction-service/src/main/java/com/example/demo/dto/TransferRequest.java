package com.example.demo.dto;


import lombok.Data;

@Data
public class TransferRequest {
    private String sourceAccount;
    private String destinationAccount;
    private Double amount;
}