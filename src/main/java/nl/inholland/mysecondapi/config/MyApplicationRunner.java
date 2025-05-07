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
        Transaction testTransaction = new Transaction(null, BigDecimal.valueOf(20.39), LocalDateTime.now(), testAtm);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(testTransaction);
        testAtm.setTransactions(transactions);
        transactionService.createTransaction(testTransaction);

        Account account = new Account(null,null,"IBAN123456789","123456789",BigDecimal.valueOf(1000),BigDecimal.valueOf(500),AccountType.CHECKING,AccountStatus.ACTIVE,LocalDateTime.now(),LocalDateTime.now());
        List<Account> accounts = new ArrayList<>();
        accounts.add(account);
        User user = new User(null, "Manon", "Dekker", "manon@example.com", "0612345678", "Test",UserRole.CUSTOMER,accounts);
        account.setOwner(user);
        userService.createUser(user);
        accountService.createAccount(account);

    }
}
