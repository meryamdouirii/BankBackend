package nl.inholland.mysecondapi.services;

import nl.inholland.mysecondapi.models.*;
import nl.inholland.mysecondapi.models.dto.TransactionDTO;
import nl.inholland.mysecondapi.models.dto.TransactionFilterRequest;
import nl.inholland.mysecondapi.models.enums.TransactionType;
import nl.inholland.mysecondapi.repositories.AccountRepository;
import nl.inholland.mysecondapi.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Account sender;
    private Account receiver;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sender = new Account();
        sender.setId(1L);
        sender.setIban("NL91CEBA0000000001");
        sender.setBalance(new BigDecimal("500.00"));
        sender.setAccountLimit(new BigDecimal("-100.00"));

        receiver = new Account();
        receiver.setId(2L);
        receiver.setIban("NL91CEBA0000000002");
        receiver.setBalance(new BigDecimal("100.00"));

        User initiator = new User();
        initiator.setFirstName("John");
        initiator.setLastName("Doe");

        transaction = new Transaction();
        transaction.setSender_account(sender);
        transaction.setReciever_account(receiver);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setInitiator(initiator);
        transaction.setTransaction_type(TransactionType.PAYMENT);
    }

    @Test
    void createTransaction_ShouldTransferMoneyAndReturnDTO() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(accountRepository.findByIban("NL91CEBA0000000002")).thenReturn(Optional.of(receiver));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        TransactionDTO result = transactionService.createTransaction(transaction);

        assertEquals(sender.getId(), result.getSender_id());
        assertEquals(receiver.getIban(), result.getReceiver_iban());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        verify(accountRepository).save(sender);
        verify(accountRepository).save(receiver);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createTransaction_ShouldThrowExceptionWhenAmountIsNull() {
        transaction.setAmount(null);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(accountRepository.findByIban(anyString())).thenReturn(Optional.of(receiver));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            transactionService.createTransaction(transaction);
        });

        assertEquals("Transaction amount is required", ex.getMessage());
    }

    @Test
    void createTransaction_ShouldThrowExceptionWhenInsufficientBalance() {
        sender.setBalance(new BigDecimal("50.00"));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(accountRepository.findByIban(anyString())).thenReturn(Optional.of(receiver));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            transactionService.createTransaction(transaction);
        });

        assertEquals("Insufficient balance", ex.getMessage());
    }

    @Test
    void createTransaction_ShouldThrowExceptionWhenReceiverIbanIsMissing() {
        transaction.getReciever_account().setIban(null);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(sender));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            transactionService.createTransaction(transaction);
        });

        assertEquals("Receiver IBAN is required", ex.getMessage());
    }

    @Test
    void createTransaction_InternalTransfer_ShouldThrowWhenInsufficientBalance() {
        transaction.setTransaction_type(TransactionType.INTERNAL_TRANSFER);
        sender.setBalance(new BigDecimal("10.00"));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(receiver));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            transactionService.createTransaction(transaction);
        });

        assertEquals("Insufficient balance", ex.getMessage());
    }

    @Test
    void createTransaction_Withdrawal_ShouldSucceed() {
        transaction.setTransaction_type(TransactionType.WITHDRAWAL);
        transaction.setReciever_account(null);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        TransactionDTO result = transactionService.createTransaction(transaction);

        assertEquals(new BigDecimal("100.00"), result.getAmount());
        verify(accountRepository).save(sender);
    }

    @Test
    void createTransaction_Deposit_ShouldThrowIfReceiverNotFound() {
        transaction.setTransaction_type(TransactionType.DEPOSIT);
        transaction.setSender_account(null);

        when(accountRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            transactionService.createTransaction(transaction);
        });

        assertEquals("Receiver account not found", ex.getMessage());
    }

    @Test
    void getAllTransactions_ShouldReturnListOfDTOs() {
        transaction.setId(1L);
        transaction.setDateTime(LocalDateTime.now());

        when(transactionRepository.findAll()).thenReturn(List.of(transaction));

        List<TransactionDTO> result = transactionService.getAllTransactions();

        assertEquals(1, result.size());
        assertEquals(transaction.getSender_account().getId(), result.get(0).getSender_id());
    }

    @Test
    void getTransactionById_ShouldReturnTransactionIfFound() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        Optional<Transaction> result = transactionService.getTransactionById(1);
        assertTrue(result.isPresent());
        assertEquals(transaction, result.get());
    }

    @Test
    void deleteTransaction_ShouldCallRepository() {
        transactionService.deleteTransaction(1);
        verify(transactionRepository).deleteById(1L);
    }

    @Test
    void updateTransaction_ShouldUpdateAndReturnTransaction() {
        transaction.setId(1L);
        transaction.setDateTime(LocalDateTime.now());

        Transaction updated = new Transaction();
        updated.setAmount(new BigDecimal("200.00"));
        updated.setDateTime(LocalDateTime.now());

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction result = transactionService.updateTransaction(1, updated);
        assertEquals(updated.getAmount(), result.getAmount());
    }

    @Test
    void getTransactionsByAccountId_ShouldReturnFilteredPage() {
        TransactionFilterRequest filter = new TransactionFilterRequest();
        Page<Transaction> txPage = new PageImpl<>(List.of(transaction));

        when(transactionRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(txPage);

        Page<TransactionDTO> result = transactionService.getTransactionsByAccountId(1L, filter, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(transaction.getSender_account().getId(), result.getContent().get(0).getSender_id());
    }
}
