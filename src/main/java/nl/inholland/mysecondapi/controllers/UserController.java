package nl.inholland.mysecondapi.controllers;

import nl.inholland.mysecondapi.models.Account;
import nl.inholland.mysecondapi.models.User;
import nl.inholland.mysecondapi.models.dto.*;
import nl.inholland.mysecondapi.models.enums.ApprovalStatus;
import nl.inholland.mysecondapi.models.enums.UserRole;
import nl.inholland.mysecondapi.services.UserService;
import nl.inholland.mysecondapi.services.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AccountService accountService;

    public UserController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_EMPLOYEE')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody RegisterRequestDTO registerRequestDTO) {
        User user = new User(
                null,
                registerRequestDTO.getFirstName(),
                registerRequestDTO.getLastName(),
                registerRequestDTO.getBsn(),
                registerRequestDTO.getEmail(),
                registerRequestDTO.getPhoneNumber(),
                registerRequestDTO.getPassword(),
                BigDecimal.valueOf(0), // daily limit
                UserRole.ROLE_CUSTOMER,
                true,
                ApprovalStatus.PENDING,
                null,
                null
        );
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_EMPLOYEE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return userService.login(loginRequestDTO);
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_EMPLOYEE')")
    @PostMapping("/deny/{id}")
    public ResponseEntity<User> denyUser(@PathVariable Long id) {
        return this.userService.getUserEntityById(id).map(user -> {
            user.setApproval_status(ApprovalStatus.DECLINED);
            userService.updateUser(id, user);
            return ResponseEntity.ok(user);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_EMPLOYEE')")
    @PostMapping("/approve/{id}")
    public ResponseEntity<User> handleUserRequest(@RequestBody UserRequestDTO request, @PathVariable Long id) {
        System.out.println("Received request: " + request);

        return userService.getUserEntityById(id).map(user -> {
            user.setApproval_status(ApprovalStatus.ACCEPTED);
            user.setDaily_limit(request.getDailyLimit());

            User updatedUser = userService.updateUser(id, user);

            List<Account> createdAccounts = this.accountService.createStarterAccounts(
                    updatedUser,
                    request.getAbsoluteLimitCheckings(),
                    request.getAbsoluteLimitSavings()
            );

            for (Account account : createdAccounts) {
                updatedUser.addAccount(account);
            }

            updatedUser = userService.updateUser(id, updatedUser);

            return ResponseEntity.ok(updatedUser);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/find")
    public ResponseEntity<?> findAccountsByName(@RequestParam String name) {
        if (name == null || name.trim().length() < 3) {
            return ResponseEntity
                    .badRequest()
                    .body("The name must be at least 3 characters long.");
        }

        FindCustomerRequestDTO request = new FindCustomerRequestDTO();
        request.setName(name.trim());
        return ResponseEntity.ok(userService.findByName(request));
    }

}
