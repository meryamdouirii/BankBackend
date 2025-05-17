package nl.inholland.mysecondapi.services;

import nl.inholland.mysecondapi.models.Account;
import nl.inholland.mysecondapi.models.User;
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
    public Optional<Account> getAccountById(Long id) {return accountRepository.findById(id);}

    @Override
    public Account createAccount(Account account) { return this.accountRepository.save(account); }

    @Override
    public Account updateAccount(Long id, Account updatedAccount) {
        return this.accountRepository.findById(id).map(existingAccount -> {
            existingAccount.setUpdatedAt(LocalDateTime.now());
            existingAccount.setAccountLimit(updatedAccount.getAccountLimit());
            //bijwerken adhv transacties in backend
            //genereer iban in backend
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

    @Override
    public List<Account> createStarterAccounts(User user, BigDecimal absoluteLimitCheckings, BigDecimal dailyLimitCheckings, BigDecimal absoluteLimitSavings, BigDecimal dailyLimitSavings) {
        List<String> usedIbans = this.getAllAcounts().stream()
                .map(Account::getIBAN)
                .toList();
        Account savingsAccount = new Account( //Create new savings account
                null,
                user,
                ibanGenerator.generateIban(usedIbans),
                BigDecimal.valueOf(0),
                absoluteLimitSavings,
                dailyLimitSavings,
                AccountType.SAVINGS,
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                null
        );
        Account checkingAccount = new Account(
                null,
                user,
                ibanGenerator.generateIban(usedIbans),
                BigDecimal.valueOf(0),
                absoluteLimitCheckings,
                dailyLimitCheckings,
                AccountType.CHECKING,
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                null
        );
        List<Account> accounts = new ArrayList<>();
        accounts.add(checkingAccount);
        accounts.add(savingsAccount);

        for (Account account : accounts) {
            this.accountRepository.save(account);
        }

        return accounts;
    }


}
