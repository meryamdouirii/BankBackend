package nl.inholland.mysecondapi.services;

import nl.inholland.mysecondapi.models.Atm;
import nl.inholland.mysecondapi.repositories.AtmRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AtmServiceImpl implements AtmService {

    private final AtmRepository atmRepository;

    public AtmServiceImpl(AtmRepository atmRepository) {
        this.atmRepository = atmRepository;
    }

    @Override
    public List<Atm> getAllAtms() {
        return atmRepository.findAll();
    }

    @Override
    public Optional<Atm> getAtmById(Long id) {
        return atmRepository.findById(id);
    }

    @Override
    public Atm createAtm(Atm atm) {
        return atmRepository.save(atm);
    }

    @Override
    public Atm updateAtm(Long id, Atm updatedAtm) {
        return atmRepository.findById(id)
                .map(existingAtm -> {
                    existingAtm.setBalance(updatedAtm.getBalance());
                    existingAtm.setLocation(updatedAtm.getLocation());
                    existingAtm.setBank(updatedAtm.getBank());
                    return atmRepository.save(existingAtm);
                })
                .orElseThrow(() -> new RuntimeException("ATM not found"));
    }

    @Override
    public void deleteAtm(Long id) {
        atmRepository.deleteById(id);
    }
}
