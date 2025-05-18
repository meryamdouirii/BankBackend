package nl.inholland.mysecondapi.controllers;

import nl.inholland.mysecondapi.models.Account;
import nl.inholland.mysecondapi.models.User;
import nl.inholland.mysecondapi.models.dto.LoginRequestDTO;
import nl.inholland.mysecondapi.models.dto.LoginResponseDTO;
import nl.inholland.mysecondapi.models.dto.RegisterRequestDTO;
import nl.inholland.mysecondapi.models.dto.UserRequestDTO;
import nl.inholland.mysecondapi.models.enums.ApprovalStatus;
import nl.inholland.mysecondapi.models.enums.UserRole;
import nl.inholland.mysecondapi.services.UserService;
import nl.inholland.mysecondapi.services.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Get a user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
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
                UserRole.ROLE_CUSTOMER, // role
                true, // is_active
                ApprovalStatus.PENDING, // approval_status
                null // accounts
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

    @PostMapping("/request")
    public ResponseEntity<User> handleUserRequest(@RequestBody UserRequestDTO request) {
        System.out.println("Received request: " + request);
        Long userId = request.getUserId();
        Boolean confirmed = request.getConfirmed();

        return userService.getUserById(userId).map(user -> {
            if (confirmed != null && confirmed) {
                user.setApproval_status(ApprovalStatus.ACCEPTED);
                User updatedUser = userService.updateUser(userId, user);

                List<Account> createdAccounts = this.accountService.createStarterAccounts(
                        updatedUser,
                        request.getAbsoluteLimitCheckings(),
                        request.getDailyLimitCheckings(),
                        request.getAbsoluteLimitSavings(),
                        request.getDailyLimitSavings()
                );

                for (Account account : createdAccounts) {
                    updatedUser.addAccount(account);
                }

                // update user met accounts
                updatedUser = userService.updateUser(userId, updatedUser);

                return ResponseEntity.ok(updatedUser);
            } else {
                user.setApproval_status(ApprovalStatus.DECLINED);
                userService.updateUser(userId, user);
                return ResponseEntity.ok(user);
            }

        }).orElseGet(() -> ResponseEntity.notFound().build());

    }



}
