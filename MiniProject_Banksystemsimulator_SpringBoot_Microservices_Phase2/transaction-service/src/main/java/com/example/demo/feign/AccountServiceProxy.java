package com.example.demo.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ACCOUNT-SERVICE") 
public interface AccountServiceProxy {
    // Amount is positive for deposit, negative for withdrawal/transfer source
    @PutMapping("/api/accounts/{accountNumber}/balance")
    void updateBalance(@PathVariable("accountNumber") String accountNumber, @RequestBody Double amount);
}