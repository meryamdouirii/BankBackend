package nl.inholland.mysecondapi.models;

import nl.inholland.mysecondapi.models.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    private Transaction transaction;
    private Account senderAccount;
    private Account receiverAccount;
    private User initiator;

    @BeforeEach
    void setUp() {
        senderAccount = new Account();
        senderAccount.setId(1L);
        senderAccount.setIban("NL01INHO0000000001");

        receiverAccount = new Account();
        receiverAccount.setId(2L);
        receiverAccount.setIban("NL01INHO0000000002");

        initiator = new User();
        initiator.setId(100L);
        initiator.setFirstName("John");
        initiator.setLastName("Doe");

        transaction = new Transaction();
        transaction.setId(10L);
        transaction.setSender_account(senderAccount);
        transaction.setReciever_account(receiverAccount);
        transaction.setAmount(new BigDecimal("250.00"));
        transaction.setDateTime(LocalDateTime.now());
        transaction.setInitiator(initiator);
        transaction.setDescription("Test transaction");
        transaction.setTransaction_type(TransactionType.PAYMENT);
    }

    @Test
    void testTransactionFields() {
        assertEquals(10L, transaction.getId());
        assertEquals(senderAccount, transaction.getSender_account());
        assertEquals(receiverAccount, transaction.getReciever_account());
        assertEquals(new BigDecimal("250.00"), transaction.getAmount());
        assertNotNull(transaction.getDateTime());
        assertEquals(initiator, transaction.getInitiator());
        assertEquals("Test transaction", transaction.getDescription());
        assertEquals(TransactionType.PAYMENT, transaction.getTransaction_type());
    }

    @Test
    void testSettersAndGetters() {
        transaction.setAmount(new BigDecimal("500.00"));
        assertEquals(new BigDecimal("500.00"), transaction.getAmount());

        transaction.setDescription("Updated description");
        assertEquals("Updated description", transaction.getDescription());

        transaction.setTransaction_type(TransactionType.DEPOSIT);
        assertEquals(TransactionType.DEPOSIT, transaction.getTransaction_type());
    }
}
