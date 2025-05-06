package nl.inholland.mysecondapi.config;

import nl.inholland.mysecondapi.models.Account;
import nl.inholland.mysecondapi.models.Atm;
import nl.inholland.mysecondapi.models.Transaction;
import nl.inholland.mysecondapi.models.User;
import nl.inholland.mysecondapi.models.enums.*;
import nl.inholland.mysecondapi.services.AccountService;
import nl.inholland.mysecondapi.services.AtmService;
import nl.inholland.mysecondapi.services.TransactionService;
import nl.inholland.mysecondapi.services.UserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MyApplicationRunner implements ApplicationRunner {

    private final AtmService atmService;
    private final TransactionService transactionService;
    private final UserService userService;
    private final AccountService accountService;

    public MyApplicationRunner(AtmService atmService,  TransactionService transactionService, UserService userService, AccountService accountService) {
        this.atmService = atmService;
        this.transactionService = transactionService;
        this.userService = userService;
        this.accountService = accountService;
    }

    //add dummy data to database
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Atm testAtm = new Atm(null, BigDecimal.valueOf(10), "Test", "TestBank", null);
        atmService.createAtm(testAtm);
        transactionService.createTransaction(new Transaction(null, BigDecimal.valueOf(20.39), LocalDateTime.now(), testAtm));
        Account account = new Account();
        account.setType(AccountType.CHECKING);
        account.setStatus(AccountStatus.ACTIVE);
        account.setAccountNumber("123456789");
        account.setIBAN("IBAN123456789");
        account.setBalance(BigDecimal.valueOf(1000));
        account.setAccountLimit(BigDecimal.valueOf(500));
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        accountService.createAccount(account);
        List<Account> accounts = new ArrayList<>();
        accounts.add(account);
        User user = new User(null, "Manon", "Dekker", "manon@example.com", "0612345678", "Test", accounts);
        userService.createUser(user);
        List<User> owners = new ArrayList<>();
        owners.add(user);
        account.setOwners(owners);

        userService.createUser(user);


    }
}
