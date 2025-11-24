package com.example.demo.repository;

import com.example.demo.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findBySourceAccountOrDestinationAccount(String sourceAccount, String destinationAccount);
}
