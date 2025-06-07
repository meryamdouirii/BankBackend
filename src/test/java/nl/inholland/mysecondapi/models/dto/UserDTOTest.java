package nl.inholland.mysecondapi.models.dto;

import nl.inholland.mysecondapi.models.User;
import nl.inholland.mysecondapi.models.enums.ApprovalStatus;
import nl.inholland.mysecondapi.models.enums.UserRole;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class UserDTOTest {

    @Test
    void testConstructorWithUser() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setBsn("987654321");
        user.setEmail("jane@example.com");
        user.setPhoneNumber("0612345678");
        user.setDaily_limit(BigDecimal.valueOf(500));
        user.setRole(UserRole.ROLE_CUSTOMER);
        user.setActive(true);
        user.setApproval_status(ApprovalStatus.PENDING);

        UserDTO dto = new UserDTO(user);

        assertEquals("Jane", dto.getFirstName());
        assertEquals(UserRole.ROLE_CUSTOMER, dto.getRole());
    }
}
