package nl.inholland.mysecondapi.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Atm {

    @Id
    @GeneratedValue
    private int id;

    private BigDecimal balance;
    private String location;
    private String bank;

    @OneToMany(mappedBy = "atm")
    private List<Transaction> transactions;
    //een atm heeft meerdere transacties

}
