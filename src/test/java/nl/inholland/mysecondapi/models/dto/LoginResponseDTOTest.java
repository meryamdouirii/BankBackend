package nl.inholland.mysecondapi.models.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginResponseDTOTest {

    @Test
    void testConstructorAndGetter() {
        LoginResponseDTO dto = new LoginResponseDTO("token123");
        assertEquals("token123", dto.getToken());
    }
}
