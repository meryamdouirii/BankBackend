package nl.inholland.mysecondapi.controllers;

import nl.inholland.mysecondapi.models.Account;
import nl.inholland.mysecondapi.models.dto.AccountDTO;
import nl.inholland.mysecondapi.services.AccountService;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final ModelMapper modelMapper;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_EMPLOYEE')")
    @GetMapping // implement model mapper
    public ResponseEntity<List<Account>> getAllAccounts() {return ResponseEntity.ok(this.accountService.getAllAcounts());}


    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id)
                .map(account -> ResponseEntity.ok(modelMapper.map(account, AccountDTO.class)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_EMPLOYEE')")
    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {return ResponseEntity.ok(this.accountService.createAccount(account));}

    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_EMPLOYEE')")
    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody Account account) {
        return ResponseEntity.ok(accountService.updateAccount(id,account));
    }


}
