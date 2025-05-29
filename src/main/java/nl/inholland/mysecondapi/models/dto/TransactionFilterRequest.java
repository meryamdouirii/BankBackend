package nl.inholland.mysecondapi.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionFilterRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal amount;
    private AmountFilterType amountFilterType; // NEW: enum for filter type
    private String ibanContains;

    // Enum for amount filter type
    public enum AmountFilterType {
        GREATER,
        LESS,
        EQUAL
    }

}
