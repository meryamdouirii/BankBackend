package nl.inholland.mysecondapi.models.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class UserRequestDTOTest {

    @Test
    void testConstructorAndGetters() {
        UserRequestDTO dto = new UserRequestDTO(
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(2000),
                BigDecimal.valueOf(500)
        );

        assertEquals(BigDecimal.valueOf(2000), dto.getAbsoluteLimitSavings());
        assertEquals(BigDecimal.valueOf(500), dto.getDailyLimit());
    }
}
