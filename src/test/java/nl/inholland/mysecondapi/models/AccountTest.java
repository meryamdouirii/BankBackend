package nl.inholland.mysecondapi.models;

import nl.inholland.mysecondapi.models.enums.AccountStatus;
import nl.inholland.mysecondapi.models.enums.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Account account;
    private User owner;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(10L);
        owner.setFirstName("Test");
        owner.setLastName("User");

        account = new Account();
        account.setId(1L);
        account.setOwner(owner);
        account.setIban("NL01INHO0000000001");
        account.setBalance(new BigDecimal("1000.50"));
        account.setAccountLimit(new BigDecimal("5000.00"));
        account.setType(AccountType.CHECKING);
        account.setStatus(AccountStatus.ACTIVE);
        account.setCreatedAt(LocalDateTime.now().minusDays(1));
        account.setUpdatedAt(LocalDateTime.now());

        account.setOutgoingTransactions(new ArrayList<>());
        account.setIncomingTransactions(new ArrayList<>());
    }

    @Test
    void testAccountProperties() {
        assertEquals(1L, account.getId());
        assertEquals(owner, account.getOwner());
        assertEquals("NL01INHO0000000001", account.getIban());
        assertEquals(new BigDecimal("1000.50"), account.getBalance());
        assertEquals(new BigDecimal("5000.00"), account.getAccountLimit());
        assertEquals(AccountType.CHECKING, account.getType());
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
        assertNotNull(account.getCreatedAt());
        assertNotNull(account.getUpdatedAt());
    }

    @Test
    void testTransactionLists() {
        assertNotNull(account.getOutgoingTransactions());
        assertNotNull(account.getIncomingTransactions());

        account.getOutgoingTransactions().add(new Transaction());
        account.getIncomingTransactions().add(new Transaction());

        assertEquals(1, account.getOutgoingTransactions().size());
        assertEquals(1, account.getIncomingTransactions().size());
    }

    @Test
    void testOwnerRelation() {
        User newOwner = new User();
        newOwner.setId(20L);
        account.setOwner(newOwner);
        assertEquals(newOwner, account.getOwner());
    }

    @Test
    void testUpdateTimestamps() {
        LocalDateTime now = LocalDateTime.now();
        account.setCreatedAt(now);
        account.setUpdatedAt(now);

        assertEquals(now, account.getCreatedAt());
        assertEquals(now, account.getUpdatedAt());
    }
}
