package nl.inholland.mysecondapi.config;

import nl.inholland.mysecondapi.models.*;
import nl.inholland.mysecondapi.models.enums.*;
import nl.inholland.mysecondapi.services.AccountService;
import nl.inholland.mysecondapi.services.IbanGenerator;
import nl.inholland.mysecondapi.services.TransactionService;
import nl.inholland.mysecondapi.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.ApplicationArguments;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MyApplicationRunnerTest {

    private TransactionService transactionService;
    private UserService userService;
    private AccountService accountService;
    private IbanGenerator ibanGenerator;
    private MyApplicationRunner runner;

    @BeforeEach
    void setup() {
        transactionService = mock(TransactionService.class);
        userService = mock(UserService.class);
        accountService = mock(AccountService.class);
        ibanGenerator = mock(IbanGenerator.class);

        runner = new MyApplicationRunner(transactionService, userService, accountService, ibanGenerator);

        when(ibanGenerator.generateIban(any())).thenReturn("NL01BANK0123456789", "NL02BANK0123456789");
        when(accountService.createAccount(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userService.createUser(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void testRun_CreatesMainUserAndAccounts() throws Exception {
        runner.run(mock(ApplicationArguments.class));

        // Capture the user creation calls
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService, atLeast(1)).createUser(userCaptor.capture());

        List<User> createdUsers = userCaptor.getAllValues();
        assert createdUsers.stream().anyMatch(u -> u.getFirstName().equals("Manon"));

        // Ensure IBANs were used for accounts
        verify(ibanGenerator, atLeast(2)).generateIban(any());

        // Ensure accounts were created
        verify(accountService, atLeast(2)).createAccount(any());
    }

    @Test
    void testRun_CreatesAccountsOnlyForAcceptedCustomers() throws Exception {
        runner.run(mock(ApplicationArguments.class));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService, atLeast(1)).createUser(userCaptor.capture());

        long acceptedCustomers = userCaptor.getAllValues().stream()
                .filter(u -> u.getRole() == UserRole.ROLE_CUSTOMER)
                .filter(u -> u.getApproval_status() == ApprovalStatus.ACCEPTED)
                .count();

        // Each accepted customer should have 2 accounts
        verify(accountService, times((int) acceptedCustomers * 2)).createAccount(any());
    }
}
