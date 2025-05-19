package nl.inholland.mysecondapi.config;

import nl.inholland.mysecondapi.models.Account;
import nl.inholland.mysecondapi.models.Transaction;
import nl.inholland.mysecondapi.models.User;
import nl.inholland.mysecondapi.models.enums.*;
import nl.inholland.mysecondapi.services.AccountService;
import nl.inholland.mysecondapi.services.TransactionService;
import nl.inholland.mysecondapi.services.UserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MyApplicationRunner implements ApplicationRunner {

    //private final TransactionService transactionService;
    private final UserService userService;
    private final AccountService accountService;

    public MyApplicationRunner(TransactionService transactionService, UserService userService, AccountService accountService) {
        //this.transactionService = transactionService;
        this.userService = userService;
        this.accountService = accountService;
    }

    //add dummy data to database
    @Override
    public void run(ApplicationArguments args) throws Exception {

        BigDecimal dailyLimit = BigDecimal.valueOf(1000);
        BigDecimal transactionLimit = BigDecimal.valueOf(500);

        Account account = new Account(
                null, null, "IBAN123456789",
                BigDecimal.valueOf(1000), BigDecimal.valueOf(0),
                AccountType.CHECKING, AccountStatus.ACTIVE,
                LocalDateTime.now(), LocalDateTime.now(), null, null
        );

        List<Account> accounts = new ArrayList<>();
        accounts.add(account);

        User user = new User(
                null, "Manon", "Dekker", "bsn123456", "manon@example.com", "0612345678", "Test",
                dailyLimit, transactionLimit,
                UserRole.ROLE_CUSTOMER, true, ApprovalStatus.ACCEPTED, accounts, null
        );

        List<User> extraUsers = List.of(
                new User(null, "Harry", "Smit", "123456789", "harry@example.com", "0612345678", "Test",
                        dailyLimit, transactionLimit,
                        UserRole.ROLE_ADMINISTRATOR, false, ApprovalStatus.PENDING, accounts, null),
                new User(null, "Sophie", "Jansen", "bsn234567", "sophie@example.com", "0612345671", "Test",
                        dailyLimit, transactionLimit,
                        UserRole.ROLE_CUSTOMER, true, ApprovalStatus.ACCEPTED, null, null),
                new User(null, "Lucas", "de Vries", "bsn345678", "lucas@example.com", "0612345672", "Test",
                        dailyLimit, transactionLimit,
                        UserRole.ROLE_CUSTOMER, false, ApprovalStatus.DECLINED, null, null),
                new User(null, "Emma", "Bakker", "bsn456789", "emma@example.com", "0612345673", "Test",
                        dailyLimit, transactionLimit,
                        UserRole.ROLE_CUSTOMER, true, ApprovalStatus.PENDING, null, null),
                new User(null, "Daan", "Visser", "bsn567890", "daan@example.com", "0612345674", "Test",
                        dailyLimit, transactionLimit,
                        UserRole.ROLE_CUSTOMER, false, ApprovalStatus.ACCEPTED, null, null),
                new User(null, "Julia", "Smit", "bsn678901", "julia@example.com", "0612345675", "Test",
                        dailyLimit, transactionLimit,
                        UserRole.ROLE_CUSTOMER, true, ApprovalStatus.DECLINED, null, null),
                new User(null, "Thomas", "Meijer", "bsn789012", "thomas@example.com", "0612345676", "Test",
                        dailyLimit, transactionLimit,
                        UserRole.ROLE_CUSTOMER, false, ApprovalStatus.PENDING, null, null),
                new User(null, "Lotte", "de Boer", "bsn890123", "lotte@example.com", "0612345677", "Test",
                        dailyLimit, transactionLimit,
                        UserRole.ROLE_CUSTOMER, true, ApprovalStatus.DECLINED, null, null),
                new User(null, "Noah", "Mulder", "bsn901234", "noah@example.com", "0612345678", "Test",
                        dailyLimit, transactionLimit,
                        UserRole.ROLE_CUSTOMER, false, ApprovalStatus.ACCEPTED, null, null)
        );

        for (User extraUser : extraUsers) {
            userService.createUser(extraUser);
        }

        account.setOwner(user);
        userService.createUser(user);

        accountService.createAccount(account);

    }


}
