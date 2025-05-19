package nl.inholland.mysecondapi.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @ManyToOne
    private Account reciever_account;
    @ManyToOne
    private Account sender_account;
    private BigDecimal amount;
    private LocalDateTime dateTime;
    @ManyToOne
    private User initiator;
    private String description;


}
