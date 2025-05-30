package nl.inholland.mysecondapi.controllers;


import io.jsonwebtoken.Jwt;
import lombok.Getter;
import lombok.Setter;
import nl.inholland.mysecondapi.models.Transaction;
import nl.inholland.mysecondapi.models.User;
import nl.inholland.mysecondapi.models.dto.TransactionDTO;
import nl.inholland.mysecondapi.models.dto.TransactionFilterRequest;
import nl.inholland.mysecondapi.services.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable; // Correct import
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAll() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getById(@PathVariable int id) {
        return transactionService.getTransactionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Transaction> create(@RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.createTransaction(transaction));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> update(@PathVariable int id, @RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, transaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/my")
    public ResponseEntity<Page<TransactionDTO>> getUserTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) BigDecimal amount,
            @RequestParam(required = false) String amountFilterType, // Changed to String
            @RequestParam(required = false) String iban,
            @PageableDefault(size = 20, sort = "dateTime", direction = Sort.Direction.DESC) Pageable pageable) {

        TransactionFilterRequest.AmountFilterType filterType = null;
        if (amountFilterType != null) {
            try {
                filterType = TransactionFilterRequest.AmountFilterType.valueOf(amountFilterType.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Handle invalid enum values if needed
            }
        }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = (Long) authentication.getDetails();
            TransactionFilterRequest filters = new TransactionFilterRequest(startDate, endDate, amount, filterType, iban);
            return ResponseEntity.ok(transactionService.getTransactionsByUser(userId, filters, pageable));
        }



    }
