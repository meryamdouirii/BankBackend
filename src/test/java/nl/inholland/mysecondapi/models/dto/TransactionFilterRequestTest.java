package nl.inholland.mysecondapi.models.dto;

import nl.inholland.mysecondapi.models.enums.AmountFilterType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionFilterRequestTest {

    @Test
    void testConstructorAndGetters() {
        TransactionFilterRequest request = new TransactionFilterRequest(
                LocalDateTime.MIN, LocalDateTime.MAX, BigDecimal.valueOf(100), AmountFilterType.GREATER, "NL01INHO0000000001"
        );

        assertEquals(BigDecimal.valueOf(100), request.getAmount());
        assertEquals(AmountFilterType.GREATER, request.getAmountFilterType());
    }
}
