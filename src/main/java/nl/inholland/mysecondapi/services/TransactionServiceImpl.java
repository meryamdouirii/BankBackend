package nl.inholland.mysecondapi.services;

import nl.inholland.mysecondapi.models.Transaction;
import nl.inholland.mysecondapi.models.dto.TransactionDTO;
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
                    return transactionRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    @Override
    public void deleteTransaction(int id) {
        transactionRepository.deleteById((long) id);
    }

    @Override
    public List<TransactionDTO> getTransactionsByUser(Long id) {
        List<Transaction> myTransactions = transactionRepository.findAllByUserId(id);
        return myTransactions.stream()
                .map(tx -> new TransactionDTO(
                        tx.getId(),
                        tx.getReciever_account().getIBAN(),       // Assuming Account has getIban()
                        tx.getSender_account().getIBAN(),                   // Passing full Account object
                        tx.getAmount(),
                        tx.getDateTime(),
                        tx.getInitiator().getFirstName() + " " + tx.getInitiator().getLastName(),      // Assuming User has getName()
                        tx.getDescription()
                ))
                .toList();
    }
}
