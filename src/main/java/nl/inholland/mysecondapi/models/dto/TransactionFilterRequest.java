package nl.inholland.mysecondapi.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TransactionFilterRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal amount;
    private AmountFilterType amountFilterType; // NEW: enum for filter type
    private String ibanContains;

    @Getter
    public enum AmountFilterType {
        GREATER(0),
        LESS(1),
        EQUAL(2);

        private final int code;
        AmountFilterType(int code) { this.code = code; }
    }

}
