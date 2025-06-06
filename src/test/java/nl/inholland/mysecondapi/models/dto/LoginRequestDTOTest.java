package nl.inholland.mysecondapi.models.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginRequestDTOTest {

    @Test
    void testGetterSetter() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("password");

        assertEquals("test@example.com", dto.getEmail());
        assertEquals("password", dto.getPassword());
    }
}
