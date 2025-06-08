package nl.inholland.mysecondapi.services;

import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.iban4j.IbanFormatException;
import org.iban4j.IbanUtil;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class IbanGenerator {

    private static final String BANK_CODE = "CEBA";
    private static final CountryCode COUNTRY_CODE = CountryCode.NL;

    private final SecureRandom random = new SecureRandom();

    public String generateIban(List<String> usedIbans) {
        Set<String> usedSet = new HashSet<>(usedIbans); // for faster lookups
        String iban;

        do {
            String accountNumber = generateRandomAccountNumber();
            iban = new Iban.Builder()
                    .countryCode(COUNTRY_CODE)
                    .bankCode(BANK_CODE)
                    .accountNumber(accountNumber)
                    .build()
                    .toFormattedString(); // includes spaces
        } while (usedSet.contains(iban));

        return iban;
    }

    private String generateRandomAccountNumber() {
        long number = Math.abs(random.nextLong()) % 1_000_000_0000L;
        return String.format("%010d", number);
    }
}
