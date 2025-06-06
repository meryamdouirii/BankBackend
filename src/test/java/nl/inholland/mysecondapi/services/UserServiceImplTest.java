package nl.inholland.mysecondapi.services;

import nl.inholland.mysecondapi.models.User;
import nl.inholland.mysecondapi.models.Account;
import nl.inholland.mysecondapi.models.enums.AccountType;
import nl.inholland.mysecondapi.models.dto.*;
import nl.inholland.mysecondapi.models.enums.ApprovalStatus;
import nl.inholland.mysecondapi.repositories.UserRepository;
import nl.inholland.mysecondapi.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Account account;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setHashed_password("password");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setActive(true);

        account = new Account();
        account.setIban("NL91ABNA0417164300");
        account.setType(AccountType.CHECKING);
        account.setOwner(user);

        List<Account> accounts = new ArrayList<>();
        accounts.add(account);
        user.setAccounts(accounts);
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<UserDTO> users = userService.getAllUsers();
        assertEquals(1, users.size());
        assertEquals("John", users.get(0).getFirstName());
    }

    @Test
    void testGetAllUsers_EmptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        List<UserDTO> users = userService.getAllUsers();
        assertTrue(users.isEmpty());
    }

    @Test
    void testGetUserById_Found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Optional<UserDTO> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<UserDTO> result = userService.getUserById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetUserEntityById_Found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Optional<User> result = userService.getUserEntityById(1L);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void testGetUserEntityById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<User> result = userService.getUserEntityById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testCreateUser() {
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.createUser(user);
        assertEquals("test@example.com", savedUser.getEmail());
        verify(userRepository).save(any(User.class));
        verify(bCryptPasswordEncoder).encode("password");
    }

    @Test
    void testCreateUser_EmailExists() {
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUser_Success() {
        User updatedUser = new User();
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Smith");
        updatedUser.setEmail("jane@example.com");
        updatedUser.setPhoneNumber("123456789");
        updatedUser.setDaily_limit(BigDecimal.valueOf(1000.0));
        updatedUser.setTransfer_limit(BigDecimal.valueOf(5000.0));
        updatedUser.setAccounts(new ArrayList<>());
        updatedUser.setApproval_status(ApprovalStatus.ACCEPTED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUser(1L, updatedUser);

        assertNotNull(result);
        verify(userRepository).save(user);
        // Verify that the user fields were updated
        assertEquals("Jane", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("jane@example.com", user.getEmail());
    }

    @Test
    void testUpdateUser_NotFound() {
        User updatedUser = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.updateUser(1L, updatedUser));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        user.setActive(true);
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any(User.class))).thenReturn(user);


        userService.deleteUser(1L);

        assertFalse(user.isActive());
        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
    }



    @Test
    void testDeleteUser_NotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.deleteUser(1L));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testFindByName_Success() {
        FindCustomerRequestDTO request = new FindCustomerRequestDTO();
        request.setName("John");

        when(userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("John", "John"))
                .thenReturn(List.of(user));

        FindCustomerResponseDTO result = userService.findByName(request);

        assertNotNull(result);
        assertEquals(1, result.getAccounts().size());
        assertEquals("NL91ABNA0417164300", result.getAccounts().get(0).getIban());
        assertEquals("CHECKING", result.getAccounts().get(0).getAccountType());
        assertEquals("John Doe", result.getAccounts().get(0).getUserName());
    }

    @Test
    void testFindByName_NoResults() {
        FindCustomerRequestDTO request = new FindCustomerRequestDTO();
        request.setName("NonExistent");

        when(userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("NonExistent", "NonExistent"))
                .thenReturn(Collections.emptyList());

        FindCustomerResponseDTO result = userService.findByName(request);

        assertNotNull(result);
        assertTrue(result.getAccounts().isEmpty());
    }

    @Test
    void testFindByName_MultipleUsersWithAccounts() {
        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Johnny");
        user2.setLastName("Johnson");

        Account account2 = new Account();
        account2.setIban("NL91ABNA0417164301");
        account2.setType(AccountType.SAVINGS);
        account2.setOwner(user2);

        user2.setAccounts(List.of(account2));

        FindCustomerRequestDTO request = new FindCustomerRequestDTO();
        request.setName("John");

        when(userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("John", "John"))
                .thenReturn(List.of(user, user2));

        FindCustomerResponseDTO result = userService.findByName(request);

        assertNotNull(result);
        assertEquals(2, result.getAccounts().size());
    }

    @Test
    void testLogin_Success() {
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtProvider.createToken(any(), any(), any())).thenReturn("token");

        LoginRequestDTO request = new LoginRequestDTO("test@example.com", "password");
        LoginResponseDTO response = userService.login(request);

        assertNotNull(response);
        assertEquals("token", response.getToken());
    }

    @Test
    void testLogin_Invalid() {
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(false);

        LoginRequestDTO request = new LoginRequestDTO("test@example.com", "wrongpass");
        assertThrows(IllegalArgumentException.class, () -> userService.login(request));
    }

    @Test
    void testLogin_UserNotFound() {
        when(userRepository.findUserByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        LoginRequestDTO request = new LoginRequestDTO("nonexistent@example.com", "password");
        assertThrows(IllegalArgumentException.class, () -> userService.login(request));

        verify(bCryptPasswordEncoder, never()).matches(anyString(), anyString());
        verify(jwtProvider, never()).createToken(any(), any(), any());
    }
}