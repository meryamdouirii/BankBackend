package nl.inholland.mysecondapi.controllers;

import nl.inholland.mysecondapi.models.Transaction;
import nl.inholland.mysecondapi.services.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
