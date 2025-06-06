package nl.inholland.mysecondapi.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.mysecondapi.models.enums.ApprovalStatus;
import nl.inholland.mysecondapi.models.enums.UserRole;

import java.math.BigDecimal;
import java.util.List;


@Entity
@Data
@Table(name = "users") //name table users, to prevent sql syntax errors
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String firstName;
    private String lastName;
    private String bsn;
    private String email;
    private String phoneNumber;
    private String hashed_password;
    private BigDecimal daily_limit;
    private BigDecimal transfer_limit;
    private UserRole role;
    private boolean active;
    private ApprovalStatus approval_status;

    @OneToMany(mappedBy="owner")
    private List<Account> accounts;
    @OneToMany(mappedBy="initiator")
    private List<Transaction> initiated_transactions;

    public List<Account> addAccount(Account account){
        this.accounts.add(account);
        return this.accounts;
    }

}
