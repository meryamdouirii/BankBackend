package nl.inholland.mysecondapi.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Transaction {

    @Id
    @GeneratedValue
    private int id;
    private BigDecimal amount;
    private LocalDateTime dateTime;

    @ManyToOne
    @JsonBackReference
    private Atm atm;

}
