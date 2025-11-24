package com.example.demo.service;


import com.example.demo.exception.InsufficientBalanceException;
import com.example.demo.model.Account;
import com.example.demo.model.Transaction;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.service.AccountService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountService accountService;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setAccountNumber("JOH1234");
        account.setHolderName("John Doe");
        account.setBalance(1000.0);
        account.setStatus("ACTIVE");
        account.setCreatedAt(new Date());
    }

    @Test
    void testCreateAccount() {
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account input = new Account();
        input.setHolderName("John Doe");
        Account created = accountService.createAccount(input);

        assertNotNull(created);
        assertEquals("John Doe", created.getHolderName());
        assertEquals(0.0, created.getBalance());
        assertEquals("ACTIVE", created.getStatus());
        assertNotNull(created.getAccountNumber());

        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testGetAccountByNumber() {
        when(accountRepository.findByAccountNumber("JOH1234")).thenReturn(Optional.of(account));

        Account found = accountService.getAccountByNumber("JOH1234");

        assertEquals("John Doe", found.getHolderName());
        verify(accountRepository, times(1)).findByAccountNumber("JOH1234");
    }

    @Test
    void testDeposit() {
        when(accountRepository.findByAccountNumber("JOH1234")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account updated = accountService.deposit("JOH1234", 500.0);

        assertEquals(1500.0, updated.getBalance());

        ArgumentCaptor<Transaction> txnCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(1)).save(txnCaptor.capture());
        assertEquals("DEPOSIT", txnCaptor.getValue().getType());
        assertEquals(500.0, txnCaptor.getValue().getAmount());
    }

    @Test
    void testWithdrawInsufficientBalance() {
        when(accountRepository.findByAccountNumber("JOH1234")).thenReturn(Optional.of(account));

        InsufficientBalanceException ex = assertThrows(
                InsufficientBalanceException.class,
                () -> accountService.withdraw("JOH1234", 2000.0)
        );

        assertEquals("Insufficient balance for withdrawal", ex.getMessage());
    }

    @Test
    void testTransferSuccess() {
        Account destAccount = new Account();
        destAccount.setAccountNumber("ANN5678");
        destAccount.setHolderName("Ann Lee");
        destAccount.setBalance(500.0);
        destAccount.setStatus("ACTIVE");
        destAccount.setCreatedAt(new Date());

        when(accountRepository.findByAccountNumber("JOH1234")).thenReturn(Optional.of(account));
        when(accountRepository.findByAccountNumber("ANN5678")).thenReturn(Optional.of(destAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        accountService.transfer("JOH1234", "ANN5678", 200.0);

        assertEquals(800.0, account.getBalance());
        assertEquals(700.0, destAccount.getBalance());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
}
