package nl.inholland.mysecondapi.models.dto;

import lombok.Data;
import nl.inholland.mysecondapi.models.Account;
import nl.inholland.mysecondapi.models.enums.AccountStatus;
import nl.inholland.mysecondapi.models.enums.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountDTO {
    private Long id;
    private String IBAN;
    private BigDecimal balance;
    private BigDecimal accountLimit;
    private AccountType type;
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long ownerId; // to reference the User entity by ID

    public AccountDTO(Account account) {
        this.id = account.getId();
        this.IBAN = account.getIBAN();
        this.balance = account.getBalance();
        this.accountLimit = account.getAccountLimit();
        this.type = account.getType();
        this.status = account.getStatus();
        this.createdAt = account.getCreatedAt();
        this.updatedAt = account.getUpdatedAt();
    }
}
