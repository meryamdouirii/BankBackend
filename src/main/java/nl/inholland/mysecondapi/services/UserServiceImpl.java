package nl.inholland.mysecondapi.services;

import nl.inholland.mysecondapi.models.User;
import nl.inholland.mysecondapi.models.dto.*;
import nl.inholland.mysecondapi.repositories.UserRepository;
import nl.inholland.mysecondapi.security.JwtProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtProvider jwtProvider;


    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtProvider jwtProvider) {
        this.userRepository = userRepository; this.bCryptPasswordEncoder = bCryptPasswordEncoder; this.jwtProvider = jwtProvider;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserDTO::new);
    }

    @Override
    public Optional<User> getUserEntityById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User createUser(User user) {
        if (!userRepository.findUserByEmail(user.getEmail()).isEmpty()) {
            throw new IllegalArgumentException("Email is already taken, please choose another one");
        }
        user.setHashed_password(bCryptPasswordEncoder.encode(user.getHashed_password()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(existingUser->{
                    existingUser.setFirstName(updatedUser.getFirstName());
                    existingUser.setLastName(updatedUser.getLastName());
                    existingUser.setDaily_limit(updatedUser.getDaily_limit());
                    existingUser.setTransfer_limit(updatedUser.getTransfer_limit());
                    existingUser.setAccounts(updatedUser.getAccounts());
                    existingUser.setApproval_status(updatedUser.getApproval_status());
                    existingUser.setEmail(updatedUser.getEmail());
                    existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
                    return userRepository.save(existingUser);
                })
                .orElseThrow(() ->new RuntimeException("User not found"));
    }

    //dont actually delete user, set status to inactive
    @Override
    public void deleteUser(Long id) {
        userRepository.findById(id)
                .map(existingUser->{
                    existingUser.set_active(false);
                    userRepository.save(existingUser);
                    return null;
                })
                .orElseThrow(() ->new RuntimeException("User not found"));
    }

    @Override
    public FindCustomerResponseDTO findByName(FindCustomerRequestDTO request) {
        // Search for users with matching first or last name
        List<User> users = userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                request.getName(),
                request.getName()
        );

        // Collect all accounts from matching users
        List<FindCustomerResponseDTO.AccountInfo> accountInfos = users.stream()
                .flatMap(user -> user.getAccounts().stream()
                        .map(account -> new FindCustomerResponseDTO.AccountInfo(
                                account.getIBAN(),
                                account.getType().toString(),
                                account.getOwner().getFirstName() + " " + account.getOwner().getLastName()
                        ))
                )
                .collect(Collectors.toList());

        return new FindCustomerResponseDTO(accountInfos);
    }
    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findUserByEmail(loginRequestDTO.getEmail()).orElse(null);

        // Check user exists and password matches
        if (user == null || !bCryptPasswordEncoder.matches(loginRequestDTO.getPassword(), user.getHashed_password())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtProvider.createToken(user.getEmail(), user.getRole(), user.getId());
        return new LoginResponseDTO(token);
    }

}

