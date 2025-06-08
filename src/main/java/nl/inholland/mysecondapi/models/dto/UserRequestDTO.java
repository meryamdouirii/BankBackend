package nl.inholland.mysecondapi.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserRequestDTO {
    private BigDecimal absoluteLimitCheckings;
    private BigDecimal absoluteLimitSavings;
    private BigDecimal dailyLimit;
}

