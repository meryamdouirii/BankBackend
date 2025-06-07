package nl.inholland.mysecondapi.controllers;

import nl.inholland.mysecondapi.models.User;
import nl.inholland.mysecondapi.models.dto.*;
import nl.inholland.mysecondapi.models.enums.ApprovalStatus;
import nl.inholland.mysecondapi.models.enums.UserRole;
import nl.inholland.mysecondapi.services.AccountService;
import nl.inholland.mysecondapi.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = {UserController.class})
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AccountService accountService;

    private ModelMapper modelMapper;

    private User userExample;

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapper();

        userExample = new User(
                1L,
                "John",
                "Doe",
                "123456789",
                "john.doe@example.com",
                "0612345678",
                "password",
                BigDecimal.ZERO,
                UserRole.ROLE_CUSTOMER,
                true,
                ApprovalStatus.PENDING,
                null,
                null
        );
    }

    @Test
    void testGetAllUsers() throws Exception {
        UserDTO userDTO = new UserDTO(userExample);
        when(userService.getAllUsers()).thenReturn(List.of(userDTO));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userDTO.getId()))
                .andExpect(jsonPath("$[0].firstName").value(userDTO.getFirstName()))
                .andExpect(jsonPath("$[0].email").value(userDTO.getEmail()));
    }

    @Test
    void testGetUserById_Found() throws Exception {
        UserDTO userDTO = new UserDTO(userExample);
        when(userService.getUserById(1L)).thenReturn(Optional.of(userDTO));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDTO.getId()))
                .andExpect(jsonPath("$.firstName").value(userDTO.getFirstName()));
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateUser() throws Exception {
        // Hardcoded JSON for RegisterRequestDTO
        String registerRequestJson = """
            {
                "firstName": "Jane",
                "lastName": "Doe",
                "phoneNumber": "0612345679",
                "bsn": "987654321",
                "email": "jane.doe@example.com",
                "password": "password123"
            }
            """;

        User createdUser = new User(
                2L,
                "Jane",
                "Doe",
                "987654321",
                "jane.doe@example.com",
                "0612345679",
                "password123",
                BigDecimal.ZERO,
                UserRole.ROLE_CUSTOMER,
                true,
                ApprovalStatus.PENDING,
                null,
                null
        );

        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdUser.getId()))
                .andExpect(jsonPath("$.email").value(createdUser.getEmail()))
                .andExpect(jsonPath("$.firstName").value(createdUser.getFirstName()));
    }

    @Test
    void testUpdateUser() throws Exception {
        // Hardcoded JSON for updated User
        String updatedUserJson = """
            {
                "id": 1,
                "firstName": "Updated",
                "lastName": "User",
                "bsn": "123456789",
                "email": "updated.user@example.com",
                "phoneNumber": "0698765432",
                "password": "newpassword",
                "balance": 0,
                "credit": 0,
                "role": "ROLE_CUSTOMER",
                "active": true,
                "approval_status": "PENDING"
            }
            """;

        User updatedUser = new User(
                1L,
                "Updated",
                "User",
                "123456789",
                "updated.user@example.com",
                "0698765432",
                "newpassword",
                BigDecimal.ZERO,
                UserRole.ROLE_CUSTOMER,
                true,
                ApprovalStatus.PENDING,
                null,
                null
        );

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedUser.getId()))
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.email").value("updated.user@example.com"));
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void testLogin() throws Exception {
        // Hardcoded JSON for login request
        String loginRequestJson = """
            {
                "email": "john.doe@example.com",
                "password": "password"
            }
            """;

        LoginResponseDTO loginResponse = new LoginResponseDTO("dummy-token");

        when(userService.login(any(LoginRequestDTO.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummy-token"));
    }

    @Test
    void testDenyUser_Found() throws Exception {
        userExample.setApproval_status(ApprovalStatus.PENDING);
        when(userService.getUserEntityById(1L)).thenReturn(Optional.of(userExample));
        when(userService.updateUser(eq(1L), any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(1);
            user.setApproval_status(ApprovalStatus.DECLINED);
            return user;
        });

        mockMvc.perform(post("/api/users/deny/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.approval_status").value("DECLINED"));
    }

    @Test
    void testDenyUser_NotFound() throws Exception {
        when(userService.getUserEntityById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/users/deny/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testApproveUser_Found() throws Exception {
        // Hardcoded JSON for UserRequestDTO
        String approveRequestJson = """
            {
                "balance": 1000,
                "credit": 2000,
                "dailyLimit": 500,
                "monthlyLimit": 1000
            }
            """;

        userExample.setApproval_status(ApprovalStatus.PENDING);
        when(userService.getUserEntityById(1L)).thenReturn(Optional.of(userExample));
        when(userService.updateUser(eq(1L), any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(1);
            user.setApproval_status(ApprovalStatus.ACCEPTED);
            return user;
        });
        when(accountService.createStarterAccounts(any(), any(), any())).thenReturn(List.of());

        mockMvc.perform(post("/api/users/approve/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(approveRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.approval_status").value("ACCEPTED"));
    }

    @Test
    void testApproveUser_NotFound() throws Exception {
        // Same JSON as approve user found
        String approveRequestJson = """
            {
                "balance": 1000,
                "credit": 2000,
                "dailyLimit": 500,
                "monthlyLimit": 1000
            }
            """;

        when(userService.getUserEntityById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/users/approve/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(approveRequestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFindAccountsByName_Valid() throws Exception {
        String name = "John";

        List<FindCustomerResponseDTO.AccountInfo> accountInfos = List.of(
                new FindCustomerResponseDTO.AccountInfo(
                        "NL01INHO0000000001",
                        "CHECKING",
                        "John Doe"
                )
        );

        FindCustomerResponseDTO responseDTO = new FindCustomerResponseDTO(accountInfos);

        when(userService.findByName(any())).thenReturn(responseDTO);

        mockMvc.perform(get("/api/users/find")
                        .param("name", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accounts[0].userName").value("John Doe"));
    }

    @Test
    void testFindAccountsByName_Invalid() throws Exception {
        mockMvc.perform(get("/api/users/find")
                        .param("name", "Jo"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("The name must be at least 3 characters long.")));
    }
}
