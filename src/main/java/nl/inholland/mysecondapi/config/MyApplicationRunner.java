package nl.inholland.mysecondapi.config;

import nl.inholland.mysecondapi.models.Account;
import nl.inholland.mysecondapi.models.Transaction;
import nl.inholland.mysecondapi.models.User;
import nl.inholland.mysecondapi.models.enums.*;
import nl.inholland.mysecondapi.services.AccountService;
import nl.inholland.mysecondapi.services.IbanGenerator;
import nl.inholland.mysecondapi.services.TransactionService;
import nl.inholland.mysecondapi.services.UserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class MyApplicationRunner implements ApplicationRunner {

    private final TransactionService transactionService;
    private final UserService userService;
    private final AccountService accountService;
    private final IbanGenerator ibanGenerator;
    private final List<String> usedIbans = new ArrayList<>();

    public MyApplicationRunner(TransactionService transactionService,
                               UserService userService,
                               AccountService accountService,
                               IbanGenerator ibanGenerator) {
        this.transactionService = transactionService;
        this.userService = userService;
        this.accountService = accountService;
        this.ibanGenerator = ibanGenerator;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        BigDecimal dailyLimit = BigDecimal.valueOf(4000);

        User mainUser = createMainUser(dailyLimit);

        createAdditionalUsers(dailyLimit);

        Account accountCheckings = createCheckingAccount(mainUser);
        Account accountSavings = createSavingsAccount(mainUser);

        createSampleTransaction(accountSavings, accountCheckings, mainUser, TransactionType.INTERNAL_TRANSFER);
    }

    private User createMainUser(BigDecimal dailyLimit) {
        User user = new User(
                null, "Manon", "Dekker", "bsn123456", "manon@example.com",
                "0612345678", "Test", dailyLimit,
                UserRole.ROLE_CUSTOMER, true, ApprovalStatus.ACCEPTED,
                new ArrayList<>(), null
        );
        return userService.createUser(user);
    }

    private void createAdditionalUsers(BigDecimal dailyLimit) {
        List<User> extraUsers = List.of(
                new User(null, "Harry", "Smit", "123456789", "harry@example.com",
                        "0612345678", "Test", dailyLimit,
                        UserRole.ROLE_ADMINISTRATOR, false, ApprovalStatus.PENDING, null, null),
                new User(null, "Sophie", "Jansen", "bsn234567", "sophie@example.com",
                        "0612345671", "Test", dailyLimit,
                        UserRole.ROLE_CUSTOMER, true, ApprovalStatus.ACCEPTED, new ArrayList<>(), null),
                new User(null, "Lucas", "de Vries", "bsn345678", "lucas@example.com",
                        "0612345672", "Test", dailyLimit,
                        UserRole.ROLE_CUSTOMER, false, ApprovalStatus.DECLINED, null, null),
                new User(null, "Emma", "Bakker", "bsn456789", "emma@example.com",
                        "0612345673", "Test", dailyLimit,
                        UserRole.ROLE_CUSTOMER, true, ApprovalStatus.PENDING, null, null),
                new User(null, "Daan", "Manon", "bsn567890", "daan@example.com",
                        "0612345674", "Test", dailyLimit,
                        UserRole.ROLE_CUSTOMER, false, ApprovalStatus.ACCEPTED, null, null),
                new User(null, "Julia", "Smit", "bsn678901", "julia@example.com",
                        "0612345675", "Test", dailyLimit,
                        UserRole.ROLE_CUSTOMER, true, ApprovalStatus.DECLINED, null, null),
                new User(null, "Thomas", "Meijer", "bsn789012", "thomas@example.com",
                        "0612345676", "Test", dailyLimit,
                        UserRole.ROLE_CUSTOMER, false, ApprovalStatus.PENDING, null, null),
                new User(null, "Lotte", "de Boer", "bsn890123", "lotte@example.com",
                        "0612345677", "Test", dailyLimit,
                        UserRole.ROLE_CUSTOMER, true, ApprovalStatus.DECLINED, null, null),
                new User(null, "Noah", "Mulder", "bsn901234", "noah@example.com",
                        "0612345678", "Test", dailyLimit,
                        UserRole.ROLE_CUSTOMER, false, ApprovalStatus.ACCEPTED, null, null)
        );

        for (User extraUser : extraUsers) {
            User savedUser = userService.createUser(extraUser);

            if (savedUser.getRole() == UserRole.ROLE_CUSTOMER && savedUser.getApproval_status() == ApprovalStatus.ACCEPTED) {
                Account checking = createCheckingAccount(savedUser);
                Account savings = createSavingsAccount(savedUser);
                if (savedUser.getAccounts() == null)
                    savedUser.setAccounts(new ArrayList<>());
                savedUser.getAccounts().add(checking);
                savedUser.getAccounts().add(savings);
            }
        }
    }

    private Account createCheckingAccount(User owner) {
        String iban = ibanGenerator.generateIban(usedIbans);
        usedIbans.add(iban);

        Account account = new Account(
                null, owner, iban,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(-400),
                AccountType.CHECKING, AccountStatus.ACTIVE,
                LocalDateTime.now(), LocalDateTime.now(), null, null
        );
        return accountService.createAccount(account);
    }

    private Account createSavingsAccount(User owner) {
        String iban = ibanGenerator.generateIban(usedIbans);
        usedIbans.add(iban);

        Account account = new Account(
                null, owner, iban,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(0),
                AccountType.SAVINGS, AccountStatus.ACTIVE,
                LocalDateTime.now(), LocalDateTime.now(), null, null
        );
        return accountService.createAccount(account);
    }

    private void createSampleTransaction(Account fromAccount, Account toAccount, User performer, TransactionType transactionType) {
        LocalDateTime startDate = LocalDateTime.now().minusYears(1);
        LocalDateTime endDate = LocalDateTime.now();

        for (int i = 0; i < 2; i++) {
            long startEpoch = startDate.toEpochSecond(java.time.ZoneOffset.UTC);
            long endEpoch = endDate.toEpochSecond(java.time.ZoneOffset.UTC);
            long randomEpoch = ThreadLocalRandom.current().nextLong(startEpoch, endEpoch);
            LocalDateTime randomDate = LocalDateTime.ofEpochSecond(randomEpoch, 0, java.time.ZoneOffset.UTC);

            Transaction transaction = new Transaction(
                    null, fromAccount, toAccount,
                    BigDecimal.valueOf(25), randomDate,
                    performer, "Test Transaction", transactionType
            );
            transactionService.createTransaction(transaction);
        }

        Transaction transaction = new Transaction(
                null, toAccount, fromAccount,
                BigDecimal.valueOf(500), LocalDateTime.now(),
                performer, "Test Transaction", transactionType
        );
        transactionService.createTransaction(transaction);
    }
}
