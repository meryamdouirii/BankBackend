package nl.inholland.mysecondapi.models.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FindCustomerRequestDTOTest {

    @Test
    void testGetterSetter() {
        FindCustomerRequestDTO dto = new FindCustomerRequestDTO();
        dto.setName("John");
        assertEquals("John", dto.getName());
    }
}
