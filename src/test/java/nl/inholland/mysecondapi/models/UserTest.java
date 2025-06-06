package nl.inholland.mysecondapi.models;

import nl.inholland.mysecondapi.models.enums.ApprovalStatus;
import nl.inholland.mysecondapi.models.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;
    private Account account;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(50L);
        user.setFirstName("Bob");
        user.setLastName("Smith");
        user.setBsn("987654321");
        user.setEmail("bob.smith@example.com");
        user.setPhoneNumber("0612345678");
        user.setHashed_password("hashedpass");
        user.setDaily_limit(new BigDecimal("2000"));
        user.setTransfer_limit(new BigDecimal("1000"));
        user.setRole(UserRole.ROLE_CUSTOMER);
        user.set_active(true);
        user.setApproval_status(ApprovalStatus.PENDING);

        user.setAccounts(new ArrayList<>());
        user.setInitiated_transactions(new ArrayList<>());

        account = new Account();
        account.setId(1L);
        account.setIban("NL01INHO0000000001");
    }

    @Test
    void testUserProperties() {
        assertEquals(50L, user.getId());
        assertEquals("Bob", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("987654321", user.getBsn());
        assertEquals("bob.smith@example.com", user.getEmail());
        assertEquals("0612345678", user.getPhoneNumber());
        assertEquals("hashedpass", user.getHashed_password());
        assertEquals(new BigDecimal("2000"), user.getDaily_limit());
        assertEquals(new BigDecimal("1000"), user.getTransfer_limit());
        assertEquals(UserRole.ROLE_CUSTOMER, user.getRole());
        assertTrue(user.is_active());
        assertEquals(ApprovalStatus.PENDING, user.getApproval_status());
    }

    @Test
    void testAddAccount() {
        user.addAccount(account);
        assertNotNull(user.getAccounts());
        assertEquals(1, user.getAccounts().size());
        assertEquals(account, user.getAccounts().get(0));
    }

    @Test
    void testSetAccountsAndTransactions() {
        var accounts = new ArrayList<Account>();
        var transactions = new ArrayList<Transaction>();

        accounts.add(account);
        user.setAccounts(accounts);
        user.setInitiated_transactions(transactions);

        assertEquals(1, user.getAccounts().size());
        assertEquals(0, user.getInitiated_transactions().size());
    }
}
