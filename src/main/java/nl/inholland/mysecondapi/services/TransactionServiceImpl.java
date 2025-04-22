package nl.inholland.mysecondapi.services;

import nl.inholland.mysecondapi.models.Transaction;
import nl.inholland.mysecondapi.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public Optional<Transaction> getTransactionById(int id) {
        return transactionRepository.findById((long) id);
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction updateTransaction(int id, Transaction updatedTransaction) {
        return transactionRepository.findById((long) id)
                .map(existing -> {
                    existing.setAmount(updatedTransaction.getAmount());
                    existing.setDateTime(updatedTransaction.getDateTime());
                    existing.setAtm(updatedTransaction.getAtm());
                    return transactionRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    @Override
    public void deleteTransaction(int id) {
        transactionRepository.deleteById((long) id);
    }
}
