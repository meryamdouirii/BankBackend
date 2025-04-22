package nl.inholland.mysecondapi.config;

import nl.inholland.mysecondapi.models.Atm;
import nl.inholland.mysecondapi.services.AtmService;
import nl.inholland.mysecondapi.services.TransactionService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.math.BigDecimal;

public class MyApplicationRunner implements ApplicationRunner {

    private final AtmService atmService;
    private final TransactionService transactionService;

    public MyApplicationRunner( AtmService atmService,  TransactionService transactionService) {
        this.atmService = atmService;
        this.transactionService = transactionService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        atmService.createAtm(new Atm(0, BigDecimal.valueOf(10), "Test", "TestBank", null));

    }
}
