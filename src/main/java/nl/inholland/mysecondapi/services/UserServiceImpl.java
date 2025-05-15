package nl.inholland.mysecondapi.services;

import nl.inholland.mysecondapi.models.Atm;
import nl.inholland.mysecondapi.models.User;
import nl.inholland.mysecondapi.models.dto.LoginRequestDTO;
import nl.inholland.mysecondapi.models.dto.LoginResponseDTO;
import nl.inholland.mysecondapi.repositories.UserRepository;
import nl.inholland.mysecondapi.security.JwtProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtProvider jwtProvider;


    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtProvider jwtProvider) {
        this.userRepository = userRepository; this.bCryptPasswordEncoder = bCryptPasswordEncoder; this.jwtProvider = jwtProvider;
    }
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User createUser(User user) {
        if (!userRepository.findUserByEmail(user.getEmail()).isEmpty()) {
            throw new IllegalArgumentException("Username is already taken");
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(existingUser->{
                    existingUser.setFirstName(updatedUser.getFirstName());
                    existingUser.setLastName(updatedUser.getLastName());
                    existingUser.setEmail(updatedUser.getEmail());
                    existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
                    return userRepository.save(existingUser);
                })
                .orElseThrow(() ->new RuntimeException("User not found"));
    }

    //dont actually delete user, set status to inactive
    @Override
    public void deleteUser(Long id) {

    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findUserByEmail(loginRequestDTO.getEmail()).orElse(null);


        if (!bCryptPasswordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Wrong password");
        }


        String token = jwtProvider.createToken(user.getEmail(), user.getRole());


        return new LoginResponseDTO(token);
    }

}

