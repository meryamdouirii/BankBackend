package nl.inholland.mysecondapi.services;

import nl.inholland.mysecondapi.models.Account;
import nl.inholland.mysecondapi.models.User;
import nl.inholland.mysecondapi.models.enums.AccountStatus;
import nl.inholland.mysecondapi.models.enums.AccountType;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AccountService {

    List<Account> getAllAcounts();
    Optional<Account> getAccountById(Long id);
    Account createAccount(Account account);
    Account updateAccount(Long id, Account account);
    void deleteAccount(Long id);
    List <Account> createStarterAccounts(User user, BigDecimal absoluteLimitCheckings, BigDecimal absoluteLimitSavings);

}
