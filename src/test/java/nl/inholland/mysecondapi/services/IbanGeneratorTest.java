package nl.inholland.mysecondapi.services;

import org.iban4j.IbanUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class IbanGeneratorTest {

    private IbanGenerator ibanGenerator;

    @BeforeEach
    void setUp() {
        ibanGenerator = new IbanGenerator();
    }

    @Test
    void generateIban_ShouldReturnValidAndFormattedIban() {
        List<String> usedIbans = new ArrayList<>();
        String iban = ibanGenerator.generateIban(usedIbans);

        // Remove spaces for validation
        String unformattedIban = iban.replaceAll("\\s+", "");

        assertDoesNotThrow(() -> IbanUtil.validate(unformattedIban));
        assertTrue(unformattedIban.startsWith("NL"), "IBAN should start with NL");
        assertEquals(18, unformattedIban.length(), "Dutch IBAN should have 18 characters");
    }

    @Test
    void generateIban_ShouldNotReturnUsedIban() {
        List<String> usedIbans = new ArrayList<>();
        usedIbans.add("NL91 CEBA 0000 0000 01");

        for (int i = 0; i < 10; i++) {
            String generatedIban = ibanGenerator.generateIban(usedIbans);
            assertFalse(usedIbans.contains(generatedIban), "Generated IBAN should not be in used list");
        }
    }

    @RepeatedTest(20)
    void generateIban_ShouldReturnUniqueIbans() {
        List<String> usedIbans = new ArrayList<>();
        Set<String> generatedIbans = new HashSet<>();

        for (int i = 0; i < 20; i++) {
            String iban = ibanGenerator.generateIban(usedIbans);
            assertFalse(generatedIbans.contains(iban), "Duplicate IBAN generated");
            generatedIbans.add(iban);
            usedIbans.add(iban);
        }
    }
}
