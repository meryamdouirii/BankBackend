package nl.inholland.mysecondapi.models.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import nl.inholland.mysecondapi.models.Account;
import nl.inholland.mysecondapi.models.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private String receiver_iban;
    private String sender_iban;
    private BigDecimal amount;
    private LocalDateTime dateTime;
    private String initiatorName;
    private String description;

    }

