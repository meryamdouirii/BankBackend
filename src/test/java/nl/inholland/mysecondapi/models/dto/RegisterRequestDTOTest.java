package nl.inholland.mysecondapi.models.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestDTOTest {

    @Test
    void testConstructorAndGetters() {
        RegisterRequestDTO dto = new RegisterRequestDTO("John", "Doe", "0612345678", "123456789", "john@example.com", "password");

        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("0612345678", dto.getPhoneNumber());
    }
}
