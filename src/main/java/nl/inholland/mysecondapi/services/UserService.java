package nl.inholland.mysecondapi.services;

import nl.inholland.mysecondapi.models.User;
import nl.inholland.mysecondapi.models.dto.*;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDTO> getAllUsers();
    Optional<UserDTO> getUserById(Long id);
    Optional<User> getUserEntityById(Long id);

    User createUser(User user);

    User updateUser(Long id, User user);
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
    //dont actually delete user, set status to inactive
    void deleteUser(Long id);
    FindCustomerResponseDTO findByName(FindCustomerRequestDTO request);
}