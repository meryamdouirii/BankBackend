package nl.inholland.mysecondapi.controllers;

import nl.inholland.mysecondapi.models.Transaction;
import nl.inholland.mysecondapi.models.dto.TransactionDTO;
import nl.inholland.mysecondapi.models.dto.TransactionFilterRequest;
import nl.inholland.mysecondapi.models.enums.TransactionType;
import nl.inholland.mysecondapi.services.AccountService;
import nl.inholland.mysecondapi.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@ContextConfiguration(classes = {TransactionController.class})
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private AccountService accountService;

    private Transaction transaction;
    private TransactionDTO transactionDTO;

    @BeforeEach
    void setup() {
        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setDateTime(LocalDateTime.now());
        transaction.setDescription("Test transaction");

        transactionDTO = new TransactionDTO(
                1L,
                10L,
                "NL01INHO0000000002",
                "NL01INHO0000000001",
                new BigDecimal("100.00"),
                transaction.getDateTime(),
                "John Doe",
                "Test transaction",
                TransactionType.PAYMENT
        );
    }

    @Test
    void testGetAllTransactions() throws Exception {
        when(transactionService.getAllTransactions()).thenReturn(List.of(transactionDTO));

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].amount").value(100.00))
                .andExpect(jsonPath("$[0].description").value("Test transaction"));
    }

    @Test
    void testGetTransactionById_Found() throws Exception {
        when(transactionService.getTransactionById(1)).thenReturn(Optional.of(transaction));

        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100.00));
    }

    @Test
    void testGetTransactionById_NotFound() throws Exception {
        when(transactionService.getTransactionById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/transactions/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateTransaction() throws Exception {
        when(transactionService.createTransaction(any(Transaction.class))).thenReturn(transactionDTO);

        String json = """
            {
                "id": 1,
                "amount": 100.00,
                "description": "Test transaction",
                "dateTime": "2024-06-01T12:00:00"
            }
        """;

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.description").value("Test transaction"));
    }

    @Test
    void testDeleteTransaction() throws Exception {
        mockMvc.perform(delete("/api/transactions/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetTransactionsForUser_AsCustomerWithAccess() throws Exception {
        Page<TransactionDTO> page = new PageImpl<>(List.of(transactionDTO));
        when(accountService.userHasAccount(anyLong(), eq(1L))).thenReturn(true);
        when(transactionService.getTransactionsByAccountId(eq(1L), any(TransactionFilterRequest.class), any()))
                .thenReturn(page);

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_CUSTOMER");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(1L, null, Collections.singleton(authority));
        auth.setDetails(1L);  // userId als details
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);


        mockMvc.perform(get("/api/transactions/account/1")
                        .param("amountFilterType", "GREATER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].amount").value(100.00));

        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetTransactionsForUser_AsCustomerWithoutAccess() throws Exception {
        when(accountService.userHasAccount(eq(1L), eq(2L))).thenReturn(false);

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_CUSTOMER");
        Authentication auth = new UsernamePasswordAuthenticationToken(1L, null, Collections.singleton(authority));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        mockMvc.perform(get("/api/transactions/account/2")
                        .param("amountFilterType", "GREATER_THAN"))
                .andExpect(status().isForbidden());

        SecurityContextHolder.clearContext();
    }

    @Test
    void testUpdateTransaction() throws Exception {
        int id = 1;
        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setId((long) id);
        updatedTransaction.setAmount(new BigDecimal("200.00"));
        updatedTransaction.setDescription("Updated transaction");

        when(transactionService.updateTransaction(eq(id), any(Transaction.class))).thenReturn(updatedTransaction);

        String json = """
        {
            "id": 1,
            "amount": 200.00,
            "description": "Updated transaction"
        }
    """;

        mockMvc.perform(put("/api/transactions/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.amount").value(200.00))
                .andExpect(jsonPath("$.description").value("Updated transaction"));
    }

    @Test
    void testGetTransactionsForUser_WithValidAmountFilterType() throws Exception {
        Long accountId = 1L;

        when(accountService.userHasAccount(anyLong(), eq(accountId))).thenReturn(true);
        when(transactionService.getTransactionsByAccountId(anyLong(), any(), any()))
                .thenReturn(Page.empty());

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_CUSTOMER");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(1L, null, Collections.singleton(authority));
        auth.setDetails(1L);  // userId als details
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);


        mockMvc.perform(get("/api/transactions/account/{id}", accountId)
                        .param("amountFilterType", "GREATER")) // valid enum
                .andExpect(status().isOk());

        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetTransactionsForUser_WithInvalidAmountFilterType() throws Exception {
        Long accountId = 1L;

        when(accountService.userHasAccount(anyLong(), eq(accountId))).thenReturn(true);
        when(transactionService.getTransactionsByAccountId(anyLong(), any(), any()))
                .thenReturn(Page.empty());

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMINISTRATOR");
        Authentication auth = new UsernamePasswordAuthenticationToken(1L, null, Collections.singleton(authority));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        mockMvc.perform(get("/api/transactions/account/{id}", accountId)
                        .param("amountFilterType", " dijwijd"))
                .andExpect(status().isBadRequest());

        SecurityContextHolder.clearContext();
    }
}
