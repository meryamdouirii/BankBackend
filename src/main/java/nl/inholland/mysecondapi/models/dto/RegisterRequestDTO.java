package nl.inholland.mysecondapi.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class RegisterRequestDTO {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String bsn;
    private String email;
    private String password;
}
