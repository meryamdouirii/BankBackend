    package nl.inholland.mysecondapi.controllers;


    import nl.inholland.mysecondapi.models.Transaction;
    import nl.inholland.mysecondapi.models.enums.AmountFilterType;
    import nl.inholland.mysecondapi.models.dto.TransactionDTO;
    import nl.inholland.mysecondapi.models.dto.TransactionFilterRequest;
    import nl.inholland.mysecondapi.services.AccountService;
    import nl.inholland.mysecondapi.services.TransactionService;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Sort;
    import org.springframework.data.web.PageableDefault;
    import org.springframework.format.annotation.DateTimeFormat;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.security.core.Authentication;
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
        private final AccountService accountService;

        public TransactionController(TransactionService transactionService, AccountService accountService) {
            this.transactionService = transactionService;
            this.accountService = accountService;
        }

        @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_EMPLOYEE')")
        @GetMapping
        public ResponseEntity<List<TransactionDTO>> getAll() {
            return ResponseEntity.ok(transactionService.getAllTransactions());
        }

        @GetMapping("/{id}")
        public ResponseEntity<Transaction> getById(@PathVariable int id) {
            return transactionService.getTransactionById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        @PostMapping
        public ResponseEntity<TransactionDTO> create(@RequestBody Transaction transaction) {
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

        @GetMapping("/account/{id}")
        public ResponseEntity<Page<TransactionDTO>> getTransactionsForUser(
                @PathVariable("id") Long accountId,
                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                @RequestParam(required = false) BigDecimal amount,
                @RequestParam(required = false) String amountFilterType,
                @RequestParam(required = false) String iban,
                @PageableDefault(size = 20, sort = "dateTime", direction = Sort.Direction.DESC) Pageable pageable
        ) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long userId = null;
            String role = null;

            if (auth != null) {
                if (auth.getDetails() instanceof Long) {
                    userId = (Long) auth.getDetails();
                }
                if (!auth.getAuthorities().isEmpty()) {
                    role = auth.getAuthorities().iterator().next().getAuthority();
                }
            }else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            if ("ROLE_CUSTOMER".equals(role)) {
                if (!accountService.userHasAccount(userId, accountId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
            return getTransactionsForAccountInternal(accountId, startDate, endDate, amount, amountFilterType, iban, pageable);
        }

        @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_EMPLOYEE')")
        @GetMapping("/all")
        public ResponseEntity<Page<TransactionDTO>> getAllFiltered(
                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                @RequestParam(required = false) BigDecimal amount,
                @RequestParam(required = false) String amountFilterType,
                @RequestParam(required = false) String iban,
                @PageableDefault(size = 20, sort = "dateTime", direction = Sort.Direction.DESC) Pageable pageable
        )  {
            AmountFilterType filterType = null;
            if (amountFilterType != null) {
                try {
                    filterType = AmountFilterType.valueOf(amountFilterType.toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid amountFilterType: " + amountFilterType);
                }
            }
            TransactionFilterRequest filter = new TransactionFilterRequest(startDate, endDate, amount, filterType, iban);
            return ResponseEntity.ok(transactionService.getAllFilteredTransactions(filter, pageable));

        }

        private static boolean canParseAmountFilterType(String value) {
            if (value == null) return false;
            try {
                AmountFilterType.valueOf(value.toUpperCase());
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        private ResponseEntity<Page<TransactionDTO>> getTransactionsForAccountInternal(
                Long accountId,
                LocalDateTime startDate,
                LocalDateTime endDate,
                BigDecimal amount,
                String amountFilterType,
                String ibanContains,
                Pageable pageable
        ) {
            AmountFilterType filterType = null;
            if (amountFilterType != null) {
                try {
                    filterType = AmountFilterType.valueOf(amountFilterType.toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid amountFilterType: " + amountFilterType);
                }
            }        TransactionFilterRequest filters = new TransactionFilterRequest(
                    startDate,
                    endDate,
                    amount,
                    filterType,
                    ibanContains
            );

            Page<TransactionDTO> page = transactionService.getTransactionsByAccountId(accountId, filters, pageable);
            return ResponseEntity.ok(page);
        }





    }

