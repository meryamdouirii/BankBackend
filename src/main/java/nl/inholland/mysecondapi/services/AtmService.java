package nl.inholland.mysecondapi.services;

import nl.inholland.mysecondapi.models.Atm;

import java.util.List;
import java.util.Optional;

public interface AtmService {
    List<Atm> getAllAtms();
    Optional<Atm> getAtmById(Long id);
    Atm createAtm(Atm atm);
    Atm updateAtm(Long id, Atm atm);
    void deleteAtm(Long id);
}
