package nl.inholland.mysecondapi.controllers;

import nl.inholland.mysecondapi.models.Account;
import nl.inholland.mysecondapi.services.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {this.accountService = accountService;}

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {return ResponseEntity.ok(this.accountService.getAllAcounts());}
}
