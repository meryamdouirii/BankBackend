package nl.inholland.mysecondapi.models.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionDTOTest {

    @Test
    void testConstructorAndGetters() {
        TransactionDTO dto = new TransactionDTO(
                1L, 2L, "NL01INHO0000000002", "NL01INHO0000000001",
                BigDecimal.valueOf(100), LocalDateTime.now(), "Initiator Name", "Description"
        );

        assertEquals("NL01INHO0000000001", dto.getSender_iban());
        assertEquals("Initiator Name", dto.getInitiatorName());
    }
}
