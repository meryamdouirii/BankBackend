package nl.inholland.mysecondapi.models.dto;

import nl.inholland.mysecondapi.models.Account;
import nl.inholland.mysecondapi.models.User;
import nl.inholland.mysecondapi.models.enums.AccountStatus;
import nl.inholland.mysecondapi.models.enums.AccountType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AccountDTOTest {

    @Test
    void testConstructorWithAccount() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBsn("123456789");

        Account account = new Account();
        account.setId(100L);
        account.setIban("NL01INHO0000000001");
        account.setBalance(BigDecimal.valueOf(500));
        account.setAccountLimit(BigDecimal.valueOf(100));
        account.setType(AccountType.CHECKING);
        account.setStatus(AccountStatus.ACTIVE);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        account.setOwner(user);

        AccountDTO dto = new AccountDTO(account);

        assertEquals(account.getId(), dto.getId());
        assertEquals("John", dto.getOwnerFirstName());
    }
}
