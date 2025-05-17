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
    private Long userId;
    private Boolean confirmed;

    private BigDecimal dailyLimitSavings;
    private BigDecimal absoluteLimitSavings;

    private BigDecimal dailyLimitCheckings;
    private BigDecimal absoluteLimitCheckings;
}

