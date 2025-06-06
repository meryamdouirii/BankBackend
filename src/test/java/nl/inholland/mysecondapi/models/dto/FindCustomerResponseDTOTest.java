package nl.inholland.mysecondapi.models.dto;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FindCustomerResponseDTOTest {

    @Test
    void testConstructorAndGetters() {
        FindCustomerResponseDTO.AccountInfo accountInfo = new FindCustomerResponseDTO.AccountInfo("IBAN123", "CHECKING", "John Doe");
        FindCustomerResponseDTO dto = new FindCustomerResponseDTO(List.of(accountInfo));

        assertEquals(1, dto.getAccounts().size());
        assertEquals("IBAN123", dto.getAccounts().get(0).getIban());
    }
}
