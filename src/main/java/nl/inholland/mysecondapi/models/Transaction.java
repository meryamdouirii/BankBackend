package nl.inholland.mysecondapi.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.mysecondapi.models.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(optional = true)
    private Account reciever_account;
    @ManyToOne(optional = true)
    private Account sender_account;
    private BigDecimal amount;
    private LocalDateTime dateTime;
    @ManyToOne
    private User initiator;
    private String description;
    private TransactionType transaction_type;


}
