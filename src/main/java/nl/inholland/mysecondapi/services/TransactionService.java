package nl.inholland.mysecondapi.services;

import nl.inholland.mysecondapi.models.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionService {
    List<Transaction> getAllTransactions();
    Optional<Transaction> getTransactionById(int id);
    Transaction createTransaction(Transaction transaction);
    Transaction updateTransaction(int id, Transaction transaction);
    void deleteTransaction(int id);
}
