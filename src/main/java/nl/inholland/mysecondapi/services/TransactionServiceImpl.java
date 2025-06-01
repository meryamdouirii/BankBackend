package nl.inholland.mysecondapi.services;

import nl.inholland.mysecondapi.controllers.TransactionController;
import nl.inholland.mysecondapi.models.Transaction;
import nl.inholland.mysecondapi.models.dto.TransactionDTO;
import nl.inholland.mysecondapi.models.dto.TransactionFilterRequest;
import nl.inholland.mysecondapi.repositories.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // Correct import
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
                    return transactionRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    @Override
    public void deleteTransaction(int id) {
        transactionRepository.deleteById((long) id);
    }

    @Override
    public Page<TransactionDTO> getTransactionsByAccountId(Long accountId, TransactionFilterRequest filters, Pageable pageable) {

        int amountFilterTypeOrdinal = filters.getAmountFilterType() != null
                ? filters.getAmountFilterType().ordinal()
                : -1;
        Page<Transaction> transactions = transactionRepository.findAllByAccountIdWithFilters(
                accountId,
                filters.getStartDate(),
                filters.getEndDate(),
                filters.getAmount(),
                amountFilterTypeOrdinal,
                filters.getIbanContains(),
                pageable
        );

        return transactions.map(this::convertToDTO);
    }


    @Override
    public Page<TransactionDTO> getTransactionsByUser(Long id, TransactionFilterRequest filters, Pageable pageable) {
        // Use getCode instead of ordinal
        int amountFilterTypeCode = filters.getAmountFilterType() != null
                ? filters.getAmountFilterType().getCode()
                : -1;

        Page<Transaction> transactions = transactionRepository.findAllByUserIdWithFilters(
                id,
                filters.getStartDate(),
                filters.getEndDate(),
                filters.getAmount(),
                amountFilterTypeCode,
                filters.getIban(), // Changed from getIbanContains() to getIban()
                pageable
        );

        return transactions.map(this::convertToDTO);
    }

    private TransactionDTO convertToDTO(Transaction tx) {
        return new TransactionDTO(
                tx.getId(),
                tx.getSender_account().getId(),
                tx.getReciever_account().getIBAN(),
                tx.getSender_account().getIBAN(),
                tx.getAmount(),
                tx.getDateTime(),
                tx.getInitiator().getFirstName() + " " + tx.getInitiator().getLastName(),
                tx.getDescription()
        );
    }
}
