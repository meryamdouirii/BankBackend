package nl.inholland.mysecondapi.services;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class IbanGenerator {
    private static final String COUNTRY_CODE = "NL"; // Consider moving to application config
    private static final String BANK_CODE = "CEBA";

    public String generateIban(List<String> usedIbans) {
        Random random = new Random();
        String iban;

        do {
            long accountNumberLong = ThreadLocalRandom.current().nextLong(0, 10_000_000_000L);//genereer een random 10 digit code
            String accountNumber = String.format("%010d", accountNumberLong);

            String rearranged = BANK_CODE + accountNumber + COUNTRY_CODE + "00"; //format om check code te berekenen

            String numericIban = convertLettersToNumbers(rearranged); // zet alle letters om naar nummers

            int mod97 = computeMod97(numericIban); //deel door 97 en haal de restwaarde op

            String checkDigits = String.format("%02d", 98 - mod97); //doe 98 - de restwaarde

            iban = COUNTRY_CODE + checkDigits + BANK_CODE + accountNumber; //format de iban string

            //voeg spaties toe
            iban = formatIban(iban);

        } while (usedIbans.contains(iban));

        return iban;
    }

    private String convertLettersToNumbers(String input) {
        StringBuilder numeric = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (Character.isLetter(c)) {
                numeric.append((int) c - 55);
            } else {
                numeric.append(c);
            }
        }
        return numeric.toString();
    }

    private int computeMod97(String numericIban) {
        int remainder = 0;
        for (char c : numericIban.toCharArray()) {
            int digit = c - '0';
            remainder = (remainder * 10 + digit) % 97;
        }
        return remainder;
    }

    private String formatIban(String iban) {
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < iban.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append(" ");
            }
            formatted.append(iban.charAt(i));
        }
        return formatted.toString();
    }
}
