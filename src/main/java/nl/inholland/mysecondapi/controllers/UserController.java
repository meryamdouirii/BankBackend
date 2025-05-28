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
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.web.bind.annotation.*;


import javax.security.sasl.AuthenticationException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AccountService accountService;

    // Constructor to inject the UserService
    public UserController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Get a user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create a new user
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody RegisterRequestDTO registerRequestDTO) { // TODO: Different Response
        User user = new User(
                null, // id
                registerRequestDTO.getFirstName(), // firstName
                registerRequestDTO.getLastName(), // lastName
                registerRequestDTO.getBsn(), // bsn
                registerRequestDTO.getEmail(), // email
                registerRequestDTO.getPhoneNumber(), // phoneNumber
                registerRequestDTO.getPassword(),
                BigDecimal.valueOf(0),//daily limit
                BigDecimal.valueOf(0),//transfer limit
                UserRole.ROLE_CUSTOMER, // role
                true, // is_active
                ApprovalStatus.PENDING, // approval_status
                null, // accounts
                null // transactions initiated
        );        return ResponseEntity.ok(userService.createUser(user));
    }

    // Update an existing user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    // Delete a user by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return userService.login(loginRequestDTO);
    }
    @PostMapping("/deny/{id}")
    public ResponseEntity<User> denyUser(@PathVariable Long id) {
        return this.userService.getUserEntityById(id).map(user ->{
            user.setApproval_status(ApprovalStatus.DECLINED);
            userService.updateUser(id, user);
            return ResponseEntity.ok(user);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<User> handleUserRequest(@RequestBody UserRequestDTO request, @PathVariable Long id) {
        System.out.println("Received request: " + request);

        return userService.getUserEntityById(id).map(user -> {
                user.setApproval_status(ApprovalStatus.ACCEPTED);
                user.setTransfer_limit(request.getTransferLimit());
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

                // update user met accounts
                updatedUser = userService.updateUser(id, updatedUser);


                return ResponseEntity.ok(updatedUser);
        }).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @GetMapping("/test-error")
    @PreAuthorize("hasRole('CUSTOMER')") // Requires ADMINISTRATOR role
    public String triggerError() {
        // This code ONLY runs if authorization succeeds
        throw new NullPointerException("test-error"); // Would return 500
    }

}
