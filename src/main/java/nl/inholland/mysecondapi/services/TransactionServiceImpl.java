package nl.inholland.mysecondapi.services;

import nl.inholland.mysecondapi.controllers.TransactionController;
import nl.inholland.mysecondapi.models.Account;
import nl.inholland.mysecondapi.models.Transaction;
import nl.inholland.mysecondapi.models.dto.TransactionDTO;
import nl.inholland.mysecondapi.models.dto.TransactionFilterRequest;
import nl.inholland.mysecondapi.repositories.AccountRepository;
import nl.inholland.mysecondapi.repositories.TransactionRepository;
import nl.inholland.mysecondapi.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // Correct import
import org.springframework.stereotype.Service;
import nl.inholland.mysecondapi.models.enums.TransactionType;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(tx -> new TransactionDTO(
                        tx.getId(),
                        tx.getSender_account().getId(),
                        tx.getReciever_account().getIban(),
                        tx.getSender_account().getIban(),
                        tx.getAmount(),
                        tx.getDateTime(),
                        tx.getInitiator().getFirstName() + " " + tx.getInitiator().getLastName(),
                        tx.getDescription()
                ))
                .toList();
    }

    @Override
    public Optional<Transaction> getTransactionById(int id) {
        return transactionRepository.findById((long) id);
    }

    @Override
    public TransactionDTO createTransaction(Transaction transaction) {
        Account sender = accountRepository.findById(transaction.getSender_account().getId())
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        Account receiver;

        if (transaction.getTransaction_type() == TransactionType.PAYMENT) {
            System.out.println(transaction.getReciever_account().getIban());

            String iban = transaction.getReciever_account().getIban();
            receiver = accountRepository.findByIban(iban)
                    .orElseThrow(() -> new RuntimeException("Recipient account not found through IBAN"));
        } else if (transaction.getTransaction_type() == TransactionType.INTERNAL_TRANSFER) {
            receiver = accountRepository.findById(transaction.getReciever_account().getId())
                    .orElseThrow(() -> new RuntimeException("Receiver account not found by ID"));
        } else {
            throw new RuntimeException("Unknown transaction type");
        }

        BigDecimal amount = transaction.getAmount();

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Update balances
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));
        accountRepository.save(sender);
        accountRepository.save(receiver);

        // Build and save transaction
        transaction.setSender_account(sender);
        transaction.setReciever_account(receiver);
        transaction.setDateTime(LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);

        // Gebruik altijd deze methode voor output:
        return convertToDTO(savedTransaction);
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
                filters.getIban(),
                pageable
        );

        return transactions.map(this::convertToDTO);
    }

    private TransactionDTO convertToDTO(Transaction tx) {
        return new TransactionDTO(
                tx.getId(),
                tx.getSender_account().getId(),
                tx.getReciever_account().getIban(),
                tx.getSender_account().getIban(),
                tx.getAmount(),
                tx.getDateTime(),
                tx.getInitiator().getFirstName() + " " + tx.getInitiator().getLastName(),
                tx.getDescription()
        );
    }
}
