package nl.inholland.mysecondapi.services;

import nl.inholland.mysecondapi.models.Account;
import nl.inholland.mysecondapi.models.User;
import nl.inholland.mysecondapi.models.dto.AccountDTO;
import nl.inholland.mysecondapi.models.enums.AccountStatus;
import nl.inholland.mysecondapi.models.enums.AccountType;
import nl.inholland.mysecondapi.repositories.AccountRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private IbanGenerator ibanGenerator;

    @InjectMocks
    private AccountServiceImpl accountService;

    private User user;
    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);

        account = new Account();
        account.setId(100L);
        account.setOwner(user);
        account.setIban("NL01TEST0001");
        account.setType(AccountType.CHECKING);
        account.setStatus(AccountStatus.ACTIVE);
        account.setBalance(BigDecimal.ZERO);
        account.setAccountLimit(BigDecimal.valueOf(100));
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getAllAccounts_shouldReturnAccounts() {
        when(accountRepository.findAll()).thenReturn(List.of(account));
        List<Account> accounts = accountService.getAllAcounts();
        assertThat(accounts).containsExactly(account);
    }

    @Test
    void getAccountById_shouldReturnDTO() {
        when(accountRepository.findById(100L)).thenReturn(Optional.of(account));
        Optional<AccountDTO> result = accountService.getAccountById(100L);
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(100L);
    }

    @Test
    void createAccount_shouldSaveAndReturnAccount() {
        when(accountRepository.save(account)).thenReturn(account);
        Account result = accountService.createAccount(account);
        assertThat(result).isEqualTo(account);
    }

    @Test
    void updateAccount_shouldModifyAndReturnUpdatedAccount() {
        Account updated = new Account();
        updated.setAccountLimit(BigDecimal.TEN);
        updated.setStatus(AccountStatus.CLOSED);
        updated.setType(AccountType.SAVINGS);

        when(accountRepository.findById(100L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        Account result = accountService.updateAccount(100L, updated);
        assertThat(result.getAccountLimit()).isEqualTo(BigDecimal.TEN);
        assertThat(result.getStatus()).isEqualTo(AccountStatus.CLOSED);
        assertThat(result.getType()).isEqualTo(AccountType.SAVINGS);
    }

    @Test
    void updateAccount_shouldThrowIfNotFound() {
        when(accountRepository.findById(100L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> accountService.updateAccount(100L, account))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Account not found");
    }

    @Test
    void deleteAccount_shouldSetStatusToClosed() {
        when(accountRepository.findById(100L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        accountService.deleteAccount(100L);
        assertThat(account.getStatus()).isEqualTo(AccountStatus.CLOSED);
    }

    @Test
    void deleteAccount_shouldThrowIfNotFound() {
        when(accountRepository.findById(100L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> accountService.deleteAccount(100L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Account not found");
    }

    @Test
    void createAccountByType_shouldReturnCorrectAccount() {
        when(accountRepository.findAll()).thenReturn(List.of(account));
        when(ibanGenerator.generateIban(anyList())).thenReturn("NL01TEST9999");

        Account result = accountService.createAccountByType(user, AccountType.SAVINGS, BigDecimal.valueOf(500));

        assertThat(result.getIban()).isEqualTo("NL01TEST9999");
        assertThat(result.getType()).isEqualTo(AccountType.SAVINGS);
        assertThat(result.getAccountLimit()).isEqualTo(BigDecimal.valueOf(500));
        assertThat(result.getOwner()).isEqualTo(user);
    }

    @Test
    void createStarterAccounts_shouldCreateTwoAccounts() {
        when(accountRepository.findAll()).thenReturn(Collections.emptyList());
        when(ibanGenerator.generateIban(anyList()))
                .thenReturn("NL01START001")
                .thenReturn("NL01START002");
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        List<Account> accounts = accountService.createStarterAccounts(user, BigDecimal.valueOf(300), BigDecimal.valueOf(200));

        assertThat(accounts).hasSize(2);
        assertThat(accounts).anyMatch(a -> a.getType() == AccountType.CHECKING && a.getAccountLimit().equals(BigDecimal.valueOf(300)));
        assertThat(accounts).anyMatch(a -> a.getType() == AccountType.SAVINGS && a.getAccountLimit().equals(BigDecimal.valueOf(200)));
    }

    @Test
    void userHasAccount_shouldReturnTrueIfMatch() {
        when(accountRepository.findById(100L)).thenReturn(Optional.of(account));
        boolean result = accountService.userHasAccount(1L, 100L);
        assertThat(result).isTrue();
    }

    @Test
    void userHasAccount_shouldReturnFalseIfNoMatch() {
        when(accountRepository.findById(100L)).thenReturn(Optional.of(account));
        boolean result = accountService.userHasAccount(2L, 100L);
        assertThat(result).isFalse();
    }

    @Test
    void userHasAccount_shouldReturnFalseIfAccountNotFound() {
        when(accountRepository.findById(100L)).thenReturn(Optional.empty());
        boolean result = accountService.userHasAccount(1L, 100L);
        assertThat(result).isFalse();
    }
}
