package nl.inholland.mysecondapi.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.mysecondapi.models.enums.UserRole;

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
    private String email;
    private String phoneNumber;
    private String password;
    private UserRole role;

    @OneToMany(mappedBy="owner")
    private List<Account> accounts;


}
