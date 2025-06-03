package nl.inholland.mysecondapi.repositories;

import nl.inholland.mysecondapi.models.Account;
import nl.inholland.mysecondapi.models.dto.AccountDTO;
import nl.inholland.mysecondapi.models.dto.UserDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {
    Optional<Account> findByIban(String iban);
}
