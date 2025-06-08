package nl.inholland.mysecondapi.services;

import nl.inholland.mysecondapi.models.Account;
import nl.inholland.mysecondapi.models.Transaction;
import nl.inholland.mysecondapi.models.User;
import nl.inholland.mysecondapi.models.dto.TransactionDTO;
import nl.inholland.mysecondapi.models.dto.TransactionFilterRequest;
import nl.inholland.mysecondapi.models.enums.TransactionType;
import nl.inholland.mysecondapi.models.enums.UserRole;
import nl.inholland.mysecondapi.repositories.AccountRepository;
import nl.inholland.mysecondapi.repositories.TransactionRepository;
import nl.inholland.mysecondapi.specifications.TransactionSpecifications;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User testUser;
    private User receiverUser;
    private User employeeUser;
    private Account senderAccount;
    private Account receiverAccount;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        // Setup test users
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setRole(UserRole.ROLE_CUSTOMER);
        testUser.setDaily_limit(new BigDecimal("1000"));

        receiverUser = new User();
        receiverUser.setId(2L);
        receiverUser.setFirstName("Jane");
        receiverUser.setLastName("Smith");
        receiverUser.setRole(UserRole.ROLE_CUSTOMER);

        employeeUser = new User();
        employeeUser.setId(3L);
        employeeUser.setFirstName("Admin");
        employeeUser.setLastName("User");
        employeeUser.setRole(UserRole.ROLE_EMPLOYEE);

        // Setup test accounts
        senderAccount = new Account();
        senderAccount.setId(1L);
        senderAccount.setIban("NL91ABNA0417164300");
        senderAccount.setBalance(new BigDecimal("1000"));
        senderAccount.setAccountLimit(new BigDecimal("-500"));
        senderAccount.setOwner(testUser);

        receiverAccount = new Account();
        receiverAccount.setId(2L);
        receiverAccount.setIban("NL91ABNA0417164301");
        receiverAccount.setBalance(new BigDecimal("500"));
        receiverAccount.setAccountLimit(new BigDecimal("-200"));
        receiverAccount.setOwner(receiverUser);

        // Setup test transaction
        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setAmount(new BigDecimal("100"));
        testTransaction.setSender_account(senderAccount);
        testTransaction.setReciever_account(receiverAccount);
        testTransaction.setInitiator(testUser);
        testTransaction.setDescription("Test transaction");
        testTransaction.setTransaction_type(TransactionType.PAYMENT);
        testTransaction.setDateTime(LocalDateTime.now());
    }

    @Test
    void updateTransaction_ShouldUpdateSuccessfully() {
        // Arrange
        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setAmount(new BigDecimal("200"));
        updatedTransaction.setDateTime(LocalDateTime.now());

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // Act
        Transaction result = transactionService.updateTransaction(1, updatedTransaction);

        // Assert
        assertNotNull(result);
        verify(transactionRepository).findById(1L);
        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void updateTransaction_ShouldThrowException_WhenTransactionNotFound() {
        // Arrange
        Transaction updatedTransaction = new Transaction();
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                transactionService.updateTransaction(1, updatedTransaction));
    }

    @Test
    void deleteTransaction_ShouldDeleteSuccessfully() {
        // Act
        transactionService.deleteTransaction(1);

        // Assert
        verify(transactionRepository).deleteById(1L);
    }

    @Test
    void getTransactionById_ShouldReturnTransaction() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));

        // Act
        Optional<Transaction> result = transactionService.getTransactionById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testTransaction.getId(), result.get().getId());
    }

    @Test
    void getAllTransactions_ShouldReturnAllTransactions() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findAll()).thenReturn(transactions);

        // Act
        List<TransactionDTO> result = transactionService.getAllTransactions();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTransaction.getId(), result.get(0).getId());
    }

    @Test
    void getTransactionsByAccountId_ShouldReturnFilteredTransactions() {
        // Arrange
        TransactionFilterRequest filters = new TransactionFilterRequest();
        Pageable pageable = mock(Pageable.class);
        Page<Transaction> transactionPage = new PageImpl<>(Arrays.asList(testTransaction));

        try (MockedStatic<TransactionSpecifications> mockedStatic = mockStatic(TransactionSpecifications.class);
             MockedStatic<Specification> specMockedStatic = mockStatic(Specification.class)) {

            Specification<Transaction> mockSpec = mock(Specification.class);
            Specification<Transaction> chainedSpec = mock(Specification.class);

            // Mock the individual specification methods
            mockedStatic.when(() -> TransactionSpecifications.accountInvolved(anyLong())).thenReturn(mockSpec);
            mockedStatic.when(() -> TransactionSpecifications.startDateAfter(any())).thenReturn(null);
            mockedStatic.when(() -> TransactionSpecifications.endDateBefore(any())).thenReturn(null);
            mockedStatic.when(() -> TransactionSpecifications.amountFilter(any(), anyInt())).thenReturn(null);
            mockedStatic.when(() -> TransactionSpecifications.ibanContains(any())).thenReturn(null);

            // Mock the Specification.where() static method and chaining
            specMockedStatic.when(() -> Specification.where(any())).thenReturn(chainedSpec);
            when(chainedSpec.and(any())).thenReturn(chainedSpec);

            when(transactionRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(transactionPage);

            // Act
            Page<TransactionDTO> result = transactionService.getTransactionsByAccountId(1L, filters, pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getContent().size());
        }
    }

    @Test
    void getAllFilteredTransactions_ShouldReturnFilteredTransactions() {
        // Arrange
        TransactionFilterRequest filters = new TransactionFilterRequest();
        Pageable pageable = mock(Pageable.class);
        Page<Transaction> transactionPage = new PageImpl<>(Arrays.asList(testTransaction));

        try (MockedStatic<TransactionSpecifications> mockedStatic = mockStatic(TransactionSpecifications.class);
             MockedStatic<Specification> specMockedStatic = mockStatic(Specification.class)) {

            Specification<Transaction> chainedSpec = mock(Specification.class);

            // Mock the individual specification methods
            mockedStatic.when(() -> TransactionSpecifications.startDateAfter(any())).thenReturn(null);
            mockedStatic.when(() -> TransactionSpecifications.endDateBefore(any())).thenReturn(null);
            mockedStatic.when(() -> TransactionSpecifications.amountFilter(any(), anyInt())).thenReturn(null);
            mockedStatic.when(() -> TransactionSpecifications.ibanContains(any())).thenReturn(null);

            // Mock the Specification.where() static method and chaining
            specMockedStatic.when(() -> Specification.where(isNull())).thenReturn(chainedSpec);
            when(chainedSpec.and(any())).thenReturn(chainedSpec);

            when(transactionRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(transactionPage);

            // Act
            Page<TransactionDTO> result = transactionService.getAllFilteredTransactions(filters, pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getContent().size());
        }
    }

    @Test
    void createTransaction_Payment_ShouldCreateSuccessfully() {
        // Arrange
        when(accountRepository.findById(senderAccount.getId())).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByIban(receiverAccount.getIban())).thenReturn(Optional.of(receiverAccount));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // Act
        TransactionDTO result = transactionService.createTransaction(testTransaction);

        // Assert
        assertNotNull(result);
        verify(accountRepository).save(senderAccount);
        verify(accountRepository).save(receiverAccount);
        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void createTransaction_Payment_ShouldThrowException_WhenInsufficientDailyLimit() {
        // Arrange
        testUser.setDaily_limit(new BigDecimal("50")); // Less than transaction amount
        testTransaction.setAmount(new BigDecimal("100"));

        when(accountRepository.findById(senderAccount.getId())).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByIban(receiverAccount.getIban())).thenReturn(Optional.of(receiverAccount));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                transactionService.createTransaction(testTransaction));
    }

    @Test
    void createTransaction_Payment_ShouldThrowException_WhenExceedsAccountLimit() {
        // Arrange
        senderAccount.setBalance(new BigDecimal("100"));
        senderAccount.setAccountLimit(new BigDecimal("50"));
        testTransaction.setAmount(new BigDecimal("100"));

        when(accountRepository.findById(senderAccount.getId())).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByIban(receiverAccount.getIban())).thenReturn(Optional.of(receiverAccount));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                transactionService.createTransaction(testTransaction));
    }

    @Test
    void createTransaction_InternalTransfer_ShouldCreateSuccessfully() {
        // Arrange
        testTransaction.setTransaction_type(TransactionType.INTERNAL_TRANSFER);

        when(accountRepository.findById(senderAccount.getId())).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findById(receiverAccount.getId())).thenReturn(Optional.of(receiverAccount));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // Act
        TransactionDTO result = transactionService.createTransaction(testTransaction);

        // Assert
        assertNotNull(result);
        verify(accountRepository).save(senderAccount);
        verify(accountRepository).save(receiverAccount);
    }

    @Test
    void createTransaction_Withdrawal_ShouldCreateSuccessfully_WhenUserIsOwner() {
        // Arrange
        testTransaction.setTransaction_type(TransactionType.WITHDRAWAL);
        testTransaction.setReciever_account(null);

        when(accountRepository.findById(senderAccount.getId())).thenReturn(Optional.of(senderAccount));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // Act
        TransactionDTO result = transactionService.createTransaction(testTransaction);

        // Assert
        assertNotNull(result);
        verify(accountRepository).save(senderAccount);
    }

    @Test
    void createTransaction_Withdrawal_ShouldCreateSuccessfully_WhenUserIsEmployee() {
        // Arrange
        testTransaction.setTransaction_type(TransactionType.WITHDRAWAL);
        testTransaction.setInitiator(employeeUser);
        testTransaction.setReciever_account(null);

        when(accountRepository.findById(senderAccount.getId())).thenReturn(Optional.of(senderAccount));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // Act
        TransactionDTO result = transactionService.createTransaction(testTransaction);

        // Assert
        assertNotNull(result);
        verify(accountRepository).save(senderAccount);
    }

    @Test
    void createTransaction_Withdrawal_ShouldThrowException_WhenUnauthorized() {
        // Arrange
        User unauthorizedUser = new User();
        unauthorizedUser.setId(999L);
        unauthorizedUser.setRole(UserRole.ROLE_CUSTOMER);

        testTransaction.setTransaction_type(TransactionType.WITHDRAWAL);
        testTransaction.setInitiator(unauthorizedUser);
        testTransaction.setReciever_account(null);

        when(accountRepository.findById(senderAccount.getId())).thenReturn(Optional.of(senderAccount));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () ->
                transactionService.createTransaction(testTransaction));
    }

    @Test
    void createTransaction_Deposit_ShouldCreateSuccessfully() {
        // Arrange
        testTransaction.setTransaction_type(TransactionType.DEPOSIT);
        testTransaction.setSender_account(null);

        when(accountRepository.findById(receiverAccount.getId())).thenReturn(Optional.of(receiverAccount));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // Act
        TransactionDTO result = transactionService.createTransaction(testTransaction);

        // Assert
        assertNotNull(result);
        verify(accountRepository).save(receiverAccount);
    }

    @Test
    void createTransaction_ShouldThrowException_WhenAmountIsNull() {
        // Arrange
        testTransaction.setAmount(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                transactionService.createTransaction(testTransaction));
    }

    @Test
    void createTransaction_ShouldThrowException_WhenAmountIsNegative() {
        // Arrange
        testTransaction.setAmount(new BigDecimal("-10"));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                transactionService.createTransaction(testTransaction));
    }

    @Test
    void createTransaction_ShouldThrowException_WhenAmountIsZero() {
        // Arrange
        testTransaction.setAmount(BigDecimal.ZERO);

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                transactionService.createTransaction(testTransaction));
    }

    @Test
    void createTransaction_ShouldThrowException_WhenSenderAccountNotFound() {
        // Arrange
        when(accountRepository.findById(senderAccount.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                transactionService.createTransaction(testTransaction));
    }

    @Test
    void createTransaction_ShouldThrowException_WhenReceiverAccountNotFound() {
        // Arrange
        when(accountRepository.findById(senderAccount.getId())).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByIban(receiverAccount.getIban())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                transactionService.createTransaction(testTransaction));
    }

    @Test
    void createTransaction_ShouldThrowException_WhenUnknownTransactionType() {
        // Arrange
        testTransaction.setTransaction_type(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                transactionService.createTransaction(testTransaction));
    }

    @Test
    void validateAccountLimit_WithPositiveLimit_ShouldWorkCorrectly() {
        // Arrange
        senderAccount.setBalance(new BigDecimal("200"));
        senderAccount.setAccountLimit(new BigDecimal("100")); // Positive limit (minimum required balance)
        testTransaction.setAmount(new BigDecimal("150")); // Would bring balance to 50, below minimum of 100

        when(accountRepository.findById(senderAccount.getId())).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByIban(receiverAccount.getIban())).thenReturn(Optional.of(receiverAccount));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                transactionService.createTransaction(testTransaction));
    }

    @Test
    void validateAccountLimit_WithNegativeLimit_ShouldWorkCorrectly() {
        // Arrange
        senderAccount.setBalance(new BigDecimal("100"));
        senderAccount.setAccountLimit(new BigDecimal("-200")); // Overdraft limit
        testTransaction.setAmount(new BigDecimal("350")); // Would bring balance to -250, exceeding overdraft of -200

        when(accountRepository.findById(senderAccount.getId())).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByIban(receiverAccount.getIban())).thenReturn(Optional.of(receiverAccount));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                transactionService.createTransaction(testTransaction));
    }
}