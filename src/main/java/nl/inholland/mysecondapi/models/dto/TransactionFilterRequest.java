package nl.inholland.mysecondapi.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nl.inholland.mysecondapi.models.enums.AmountFilterType;

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
    private String iban;



}
