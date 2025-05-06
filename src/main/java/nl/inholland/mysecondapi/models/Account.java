package nl.inholland.mysecondapi.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.mysecondapi.models.enums.AccountType;
import nl.inholland.mysecondapi.models.enums.AccountStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@Table(name="accounts")
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JsonBackReference
    private User owner;

    private String IBAN;
    private String accountNumber;
    private BigDecimal balance;
    private BigDecimal accountLimit;
    private AccountType type;
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
