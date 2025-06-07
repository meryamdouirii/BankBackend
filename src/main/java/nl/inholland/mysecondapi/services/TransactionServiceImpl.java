package nl.inholland.mysecondapi.services;

import nl.inholland.mysecondapi.controllers.TransactionController;
import nl.inholland.mysecondapi.models.Account;
import nl.inholland.mysecondapi.models.Transaction;
import nl.inholland.mysecondapi.models.dto.TransactionDTO;
import nl.inholland.mysecondapi.models.dto.TransactionFilterRequest;
import nl.inholland.mysecondapi.repositories.AccountRepository;
import nl.inholland.mysecondapi.repositories.TransactionRepository;
import nl.inholland.mysecondapi.repositories.UserRepository;
import nl.inholland.mysecondapi.specifications.TransactionSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // Correct import
import org.springframework.data.jpa.domain.Specification;
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
                .map(tx -> convertToDTO(tx))
                .toList();
    }

    @Override
    public Optional<Transaction> getTransactionById(int id) {
        return transactionRepository.findById((long) id);
    }

    @Override
    public TransactionDTO createTransaction(Transaction transaction) {
        Account sender = null;
        Account receiver = null;
        BigDecimal amount = transaction.getAmount();

        switch (transaction.getTransaction_type()) {
            case PAYMENT:
                sender = accountRepository.findById(transaction.getSender_account().getId())
                        .orElseThrow(() -> new RuntimeException("Sender account not found"));

                String iban = transaction.getReciever_account().getIban();
                if (iban == null || iban.isEmpty()) {
                    throw new RuntimeException("Receiver IBAN is required");
                }

                receiver = accountRepository.findByIban(iban)
                        .orElseThrow(() -> new RuntimeException("Recipient account not found through IBAN"));

                BigDecimal allowedLimitPayment = sender.getAccountLimit();
                BigDecimal resultingBalancePayment = sender.getBalance().subtract(amount);
                if (resultingBalancePayment.compareTo(allowedLimitPayment) < 0) {
                    throw new RuntimeException("Transfer would exceed account limit");
                }

                if (sender.getBalance().compareTo(amount) < 0)
                    throw new RuntimeException("Insufficient balance");

                sender.setBalance(sender.getBalance().subtract(amount));
                receiver.setBalance(receiver.getBalance().add(amount));

                accountRepository.save(sender);
                accountRepository.save(receiver);
                break;

            case INTERNAL_TRANSFER:
                sender = accountRepository.findById(transaction.getSender_account().getId())
                        .orElseThrow(() -> new RuntimeException("Sender account not found"));
                receiver = accountRepository.findById(transaction.getReciever_account().getId())
                        .orElseThrow(() -> new RuntimeException("Receiver account not found"));

                BigDecimal allowedLimitInternal = sender.getAccountLimit();
                BigDecimal resultingBalanceInternal = sender.getBalance().subtract(amount);
                if (resultingBalanceInternal.compareTo(allowedLimitInternal) < 0) {
                    throw new RuntimeException("Transfer would exceed account limit");
                }

                if (sender.getBalance().compareTo(amount) < 0)
                    throw new RuntimeException("Insufficient balance");

                sender.setBalance(sender.getBalance().subtract(amount));
                receiver.setBalance(receiver.getBalance().add(amount));

                accountRepository.save(sender);
                accountRepository.save(receiver);
                break;

            case WITHDRAWAL:
                sender = accountRepository.findById(transaction.getSender_account().getId())
                        .orElseThrow(() -> new RuntimeException("Sender account not found"));

                if (sender.getBalance().compareTo(amount) < 0)
                    throw new RuntimeException("Insufficient balance");

                sender.setBalance(sender.getBalance().subtract(amount));
                accountRepository.save(sender);
                break;

            case DEPOSIT:
                receiver = accountRepository.findById(transaction.getReciever_account().getId())
                        .orElseThrow(() -> new RuntimeException("Receiver account not found"));

                receiver.setBalance(receiver.getBalance().add(amount));
                accountRepository.save(receiver);
                break;

            default:
                throw new RuntimeException("Unknown transaction type");
        }

        transaction.setSender_account(sender);
        transaction.setReciever_account(receiver);
        transaction.setDateTime(LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);
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
    public Page<TransactionDTO> getTransactionsByAccountId(Long accountId, TransactionFilterRequest filters,
                                                           Pageable pageable) {
        Specification<Transaction> spec = buildTransactionSpecification(accountId, filters);
        Page<Transaction> transactions = transactionRepository.findAll(spec, pageable);
        return transactions.map(this::convertToDTO);
    }

    @Override
    public Page<TransactionDTO> getAllFilteredTransactions(TransactionFilterRequest filters, Pageable pageable) {
        Specification<Transaction> spec = buildTransactionSpecification(null, filters);
        Page<Transaction> transactions = transactionRepository.findAll(spec, pageable);
        return transactions.map(this::convertToDTO);
    }

    private Specification<Transaction> buildTransactionSpecification(Long accountId, TransactionFilterRequest filters) {
        int amountFilterTypeOrdinal = filters.getAmountFilterType() != null
                ? filters.getAmountFilterType().ordinal()
                : -1;

        Specification<Transaction> spec = Specification
                .where(accountId != null ? TransactionSpecifications.accountInvolved(accountId) : null)
                .and(TransactionSpecifications.startDateAfter(filters.getStartDate()))
                .and(TransactionSpecifications.endDateBefore(filters.getEndDate()))
                .and(TransactionSpecifications.amountFilter(filters.getAmount(), amountFilterTypeOrdinal))
                .and(TransactionSpecifications.ibanContains(filters.getIban()));

        return spec;
    }

    private TransactionDTO convertToDTO(Transaction tx) {
        Long senderAccountId = null;
        String senderIban = null;

        if (tx.getSender_account() != null) {
            senderAccountId = tx.getSender_account().getId();
            senderIban = tx.getSender_account().getIban();
        }

        String receiverIban = null;
        if (tx.getReciever_account() != null) {
            receiverIban = tx.getReciever_account().getIban();
        }

        return new TransactionDTO(
                tx.getId(),
                senderAccountId,
                receiverIban,
                senderIban,
                tx.getAmount(),
                tx.getDateTime(),
                tx.getInitiator().getFirstName() + " " + tx.getInitiator().getLastName(),
                tx.getDescription(),
                tx.getTransaction_type());
    }
}
