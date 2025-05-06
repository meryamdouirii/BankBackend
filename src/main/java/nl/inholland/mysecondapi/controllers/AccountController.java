package nl.inholland.mysecondapi.controllers;

import nl.inholland.mysecondapi.models.Account;
import nl.inholland.mysecondapi.models.Atm;
import nl.inholland.mysecondapi.services.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {this.accountService = accountService;}

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {return ResponseEntity.ok(this.accountService.getAllAcounts());}

    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody Account account) {
        return ResponseEntity.ok(accountService.updateAccount(id,account));
    }

}
