package nl.inholland.mysecondapi.models.dto;

import lombok.Data;
import nl.inholland.mysecondapi.models.Transaction;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AtmDto {

    private int id;

    private BigDecimal balance;
    private String location;
    private String bank;
    private List<Transaction> transactions;

}
