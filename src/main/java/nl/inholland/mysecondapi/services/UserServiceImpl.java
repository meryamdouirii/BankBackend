package nl.inholland.mysecondapi.services;

import nl.inholland.mysecondapi.models.Atm;
import nl.inholland.mysecondapi.models.User;
import nl.inholland.mysecondapi.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository; this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
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
}

