package nl.inholland.mysecondapi.services;

import nl.inholland.mysecondapi.models.Account;
import nl.inholland.mysecondapi.models.enums.AccountStatus;
import nl.inholland.mysecondapi.repositories.AccountRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
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
}
