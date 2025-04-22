package nl.inholland.mysecondapi.controllers;

import nl.inholland.mysecondapi.models.Atm;
import nl.inholland.mysecondapi.services.AtmService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/atms")
public class AtmController {

    private final AtmService atmService;

    public AtmController(AtmService atmService) {
        this.atmService = atmService;
    }

    @GetMapping
    public ResponseEntity<List<Atm>> getAllAtms() {
        return ResponseEntity.ok(atmService.getAllAtms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Atm> getAtmById(@PathVariable Long id) {
        //gebruik model maper
        return atmService.getAtmById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Atm> createAtm(@RequestBody Atm atm) {
        return ResponseEntity.ok(atmService.createAtm(atm));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Atm> updateAtm(@PathVariable Long id, @RequestBody Atm atm) {
        return ResponseEntity.ok(atmService.updateAtm(id, atm));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAtm(@PathVariable Long id) {
        atmService.deleteAtm(id);
        return ResponseEntity.noContent().build();
    }
}
