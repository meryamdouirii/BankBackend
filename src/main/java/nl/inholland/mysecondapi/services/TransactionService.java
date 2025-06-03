package nl.inholland.mysecondapi.services;

import nl.inholland.mysecondapi.controllers.TransactionController;
import nl.inholland.mysecondapi.models.Transaction;
import nl.inholland.mysecondapi.models.dto.TransactionDTO;
import nl.inholland.mysecondapi.models.dto.TransactionFilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // Correct import

import java.util.List;
import java.util.Optional;

public interface TransactionService {
    List<TransactionDTO> getAllTransactions();
    Optional<Transaction> getTransactionById(int id);
    Transaction createTransaction(Transaction transaction);
    Transaction updateTransaction(int id, Transaction transaction);
    void deleteTransaction(int id);
    Page<TransactionDTO> getTransactionsByUser(Long id, TransactionFilterRequest filters, Pageable pageable);
    Page<TransactionDTO> getTransactionsByAccountId(Long accountId, TransactionFilterRequest filters, Pageable pageable);

    }
