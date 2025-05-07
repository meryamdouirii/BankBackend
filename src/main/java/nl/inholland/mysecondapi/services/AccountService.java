package nl.inholland.mysecondapi.services;

import nl.inholland.mysecondapi.models.Account;


import java.util.List;
import java.util.Optional;

public interface AccountService {

    List<Account> getAllAcounts();
    Optional<Account> getAccountById(Long id);
    Account createAccount(Account account);
    Account updateAccount(Long id, Account account);
    void deleteAccount(Long id);
}
