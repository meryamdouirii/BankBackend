package nl.inholland.mysecondapi.services;

import nl.inholland.mysecondapi.models.Account;
import nl.inholland.mysecondapi.models.User;
import nl.inholland.mysecondapi.models.dto.AccountDTO;
import nl.inholland.mysecondapi.models.enums.AccountStatus;
import nl.inholland.mysecondapi.models.enums.AccountType;
import nl.inholland.mysecondapi.repositories.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final IbanGenerator ibanGenerator;

    public AccountServiceImpl(AccountRepository accountRepository, IbanGenerator ibanGenerator) {
        this.accountRepository = accountRepository;
        this.ibanGenerator = ibanGenerator;
    }

    @Override
    public List<Account> getAllAcounts() {return accountRepository.findAll();}

    @Override
    public Optional<AccountDTO> getAccountById(Long id) {
        return accountRepository.findById(id)
                .map(AccountDTO::new);
    }

    @Override
    public Account createAccount(Account account) { return this.accountRepository.save(account); }

    @Override
    public Account updateAccount(Long id, Account updatedAccount) {
        return this.accountRepository.findById(id).map(existingAccount -> {
            existingAccount.setUpdatedAt(LocalDateTime.now());
            existingAccount.setAccountLimit(updatedAccount.getAccountLimit());
            existingAccount.setStatus(updatedAccount.getStatus());  //wie kan status aanpassen? Kijk naar rollen
            existingAccount.setType(updatedAccount.getType());
            return this.accountRepository.save(existingAccount);
        }).orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Override
    public void deleteAccount(Long id) {
        this.accountRepository.findById(id).map(existingAccount -> {
            existingAccount.setStatus(AccountStatus.CLOSED);
            return this.accountRepository.save(existingAccount);
        }).orElseThrow(() -> new RuntimeException("Account not found"));
    }
    public Account createAccountByType(User user,AccountType type, BigDecimal absoluteLimit) {
        List<String> usedIbans = this.getAllAcounts().stream()
                .map(Account::getIban)
                .toList();
        return new Account(
                null,
                user,
                ibanGenerator.generateIban(usedIbans),
                BigDecimal.valueOf(0),
                absoluteLimit,
                type,
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                null
        );

    }
    @Override
    public List<Account> createStarterAccounts(User user, BigDecimal absoluteLimitCheckings, BigDecimal absoluteLimitSavings) {

        Account savingsAccount = this.createAccountByType(user, AccountType.SAVINGS, absoluteLimitSavings);
        Account checkingAccount = this.createAccountByType(user, AccountType.CHECKING, absoluteLimitCheckings);
        List<Account> accounts = new ArrayList<>();
        accounts.add(checkingAccount);
        accounts.add(savingsAccount);

        for (Account account : accounts) {
            this.accountRepository.save(account);
        }

        return accounts;
    }
    @Override
    public boolean userHasAccount(Long userId, Long accountId) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);

        if (accountOpt.isEmpty()) {
            return false;
        }

        Account account = accountOpt.get();

        return account.getOwner().getId().equals(userId); // or getCustomer().getId()
    }


}
