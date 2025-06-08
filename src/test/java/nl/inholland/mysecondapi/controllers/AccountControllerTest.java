package nl.inholland.mysecondapi.controllers;

import nl.inholland.mysecondapi.models.Account;
import nl.inholland.mysecondapi.models.User;
import nl.inholland.mysecondapi.models.dto.AccountDTO;
import nl.inholland.mysecondapi.models.enums.AccountStatus;
import nl.inholland.mysecondapi.models.enums.AccountType;
import nl.inholland.mysecondapi.security.JwtFilter;
import nl.inholland.mysecondapi.security.JwtProvider;
import nl.inholland.mysecondapi.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest
@ContextConfiguration(classes = {AccountController.class})
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    private ModelMapper modelMapper;

    private User owner;
    private Account account1, account2;

    @BeforeEach
    void setup() {
        modelMapper = new ModelMapper();

        owner = new User();
        owner.setId(100L);
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setBsn("123456789");

        account1 = new Account();
        account1.setId(1L);
        account1.setOwner(owner);
        account1.setIban("NL01INHO0000000001");
        account1.setBalance(new BigDecimal("1500.00"));
        account1.setAccountLimit(new BigDecimal("500.00"));
        account1.setType(AccountType.CHECKING);
        account1.setStatus(AccountStatus.ACTIVE);
        account1.setCreatedAt(LocalDateTime.now().minusDays(10));
        account1.setUpdatedAt(LocalDateTime.now());
        account1.setOutgoingTransactions(new ArrayList<>());
        account1.setIncomingTransactions(new ArrayList<>());

        account2 = new Account();
        account2.setId(2L);
        account2.setOwner(owner);
        account2.setIban("NL01INHO0000000002");
        account2.setBalance(new BigDecimal("3000.00"));
        account2.setAccountLimit(new BigDecimal("1000.00"));
        account2.setType(AccountType.SAVINGS);
        account2.setStatus(AccountStatus.ACTIVE);
        account2.setCreatedAt(LocalDateTime.now().minusDays(5));
        account2.setUpdatedAt(LocalDateTime.now());
        account2.setOutgoingTransactions(new ArrayList<>());
        account2.setIncomingTransactions(new ArrayList<>());
    }

    @Test
    @WithMockUser
    void testGetAllAccounts() throws Exception {
        List<Account> accounts = List.of(account1, account2);
        when(accountService.getAllAcounts()).thenReturn(accounts);

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].iban").value("NL01INHO0000000001"))
                .andExpect(jsonPath("$[0].balance").value(1500.00))
                .andExpect(jsonPath("$[0].accountLimit").value(500.00))
                .andExpect(jsonPath("$[0].type").value("CHECKING"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$[0].updatedAt").exists())
                .andExpect(jsonPath("$[0].outgoingTransactions").exists())
                .andExpect(jsonPath("$[0].incomingTransactions").exists())
                // Check second account
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].iban").value("NL01INHO0000000002"))
                .andExpect(jsonPath("$[1].balance").value(3000.00))
                .andExpect(jsonPath("$[1].accountLimit").value(1000.00))
                .andExpect(jsonPath("$[1].type").value("SAVINGS"))
                .andExpect(jsonPath("$[1].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$[0].updatedAt").exists())
                .andExpect(jsonPath("$[0].outgoingTransactions").exists())
                .andExpect(jsonPath("$[0].incomingTransactions").exists());
    }

    @Test
    @WithMockUser
    void testGetAccountById_Found() throws Exception {
        AccountDTO dto = modelMapper.map(account1, AccountDTO.class);
        when(accountService.getAccountById(1L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto.getId()))
                .andExpect(jsonPath("$.iban").value(dto.getIBAN()))
                .andExpect(jsonPath("$.balance").value(dto.getBalance().doubleValue()))
                .andExpect(jsonPath("$.accountLimit").value(dto.getAccountLimit().doubleValue()))
                .andExpect(jsonPath("$.type").value(dto.getType().name()))
                .andExpect(jsonPath("$.status").value(dto.getStatus().name()))
                .andExpect(jsonPath("$.ownerId").value(dto.getOwnerId()))
                .andExpect(jsonPath("$.ownerFirstName").value(dto.getOwnerFirstName()))
                .andExpect(jsonPath("$.ownerLastName").value(dto.getOwnerLastName()))
                .andExpect(jsonPath("$.ownerBsn").value(dto.getOwnerBsn()));
    }

    @Test
    @WithMockUser
    void testGetAccountById_NotFound() throws Exception {
        when(accountService.getAccountById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/accounts/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testCreateAccount() throws Exception {
        Account toCreate = new Account();
        toCreate.setOwner(owner);
        toCreate.setIban("NL01INHO0000000003");
        toCreate.setBalance(new BigDecimal("2000.00"));
        toCreate.setAccountLimit(new BigDecimal("800.00"));
        toCreate.setType(AccountType.CHECKING);
        toCreate.setStatus(AccountStatus.ACTIVE);
        toCreate.setCreatedAt(LocalDateTime.now());
        toCreate.setUpdatedAt(LocalDateTime.now());

        Account created = new Account();
        created.setId(3L);
        created.setOwner(owner);
        created.setIban("NL01INHO0000000003");
        created.setBalance(new BigDecimal("2000.00"));
        created.setAccountLimit(new BigDecimal("800.00"));
        created.setType(AccountType.CHECKING);
        created.setStatus(AccountStatus.ACTIVE);
        created.setCreatedAt(LocalDateTime.now());
        created.setUpdatedAt(LocalDateTime.now());

        when(accountService.createAccount(org.mockito.ArgumentMatchers.any(Account.class))).thenReturn(created);

        String json = """
            {
                "owner": { "id": 100 },
                "iban": "NL01INHO0000000003",
                "balance": 2000.00,
                "accountLimit": 800.00,
                "type": "CHECKING",
                "status": "ACTIVE"
            }
            """;

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.iban").value("NL01INHO0000000003"))
                .andExpect(jsonPath("$.balance").value(2000.00))
                .andExpect(jsonPath("$.accountLimit").value(800.00))
                .andExpect(jsonPath("$.type").value("CHECKING"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @WithMockUser
    void testUpdateAccount() throws Exception {
        Account updatedAccount = new Account();
        updatedAccount.setId(1L);
        updatedAccount.setOwner(owner);
        updatedAccount.setIban("NL01INHO0000000001");
        updatedAccount.setBalance(new BigDecimal("1800.00"));
        updatedAccount.setAccountLimit(new BigDecimal("700.00"));
        updatedAccount.setType(AccountType.CHECKING);
        updatedAccount.setStatus(AccountStatus.ACTIVE);
        updatedAccount.setCreatedAt(LocalDateTime.now().minusDays(10));
        updatedAccount.setUpdatedAt(LocalDateTime.now());

        when(accountService.updateAccount(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any(Account.class)))
                .thenReturn(updatedAccount);

        String json = """
            {
                "id": 1,
                "owner": { "id": 100 },
                "iban": "NL01INHO0000000001",
                "balance": 1800.00,
                "accountLimit": 700.00,
                "type": "CHECKING",
                "status": "ACTIVE"
            }
            """;

        mockMvc.perform(put("/api/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.balance").value(1800.00))
                .andExpect(jsonPath("$.accountLimit").value(700.00))
                .andExpect(jsonPath("$.type").value("CHECKING"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
}
