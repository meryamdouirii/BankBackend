package nl.inholland.mysecondapi.repositories;

import nl.inholland.mysecondapi.models.Atm;
import nl.inholland.mysecondapi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

}
