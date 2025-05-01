package nl.inholland.mysecondapi.config;

import nl.inholland.mysecondapi.models.Atm;
import nl.inholland.mysecondapi.services.AtmService;
import nl.inholland.mysecondapi.services.TransactionService;
import nl.inholland.mysecondapi.services.UserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.math.BigDecimal;

public class MyApplicationRunner implements ApplicationRunner {

    private final AtmService atmService;
    private final TransactionService transactionService;
    private final UserService userService;

    public MyApplicationRunner(AtmService atmService,  TransactionService transactionService, UserService userService) {
        this.atmService = atmService;
        this.transactionService = transactionService;
        this.userService = userService;
    }

    //add dummy data to database
    @Override
    public void run(ApplicationArguments args) throws Exception {
        atmService.createAtm(new Atm(null, BigDecimal.valueOf(10), "Test", "TestBank", null));

    }
}
