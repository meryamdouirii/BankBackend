package nl.inholland.mysecondapi;

import nl.inholland.mysecondapi.config.MyApplicationRunner;
import nl.inholland.mysecondapi.services.AccountService;
import nl.inholland.mysecondapi.services.TransactionService;
import nl.inholland.mysecondapi.services.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MysecondapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MysecondapiApplication.class, args);
	}


	@Bean
	public MyApplicationRunner myApplicationRunner(TransactionService transactionService, UserService userService, AccountService accountService) {
		return new MyApplicationRunner(transactionService, userService, accountService);
	}
}


