package nl.inholland.mysecondapi.repositories;

import nl.inholland.mysecondapi.models.Atm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtmRepository extends JpaRepository<Atm,Long> {

}
