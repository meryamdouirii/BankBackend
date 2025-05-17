package nl.inholland.mysecondapi.services;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class IbanGenerator {
    private static final String COUNTRY_CODE = "NL"; //put this in config
    private static final String BANK_CODE = "CEBA";


    public String generateIban(List<String> usedIbans) {
        String iban;
        do {
            String accountNumber = String.format("%010d", new Random().nextLong(1_000_000_0000L));

            String tempIban = BANK_CODE + accountNumber + COUNTRY_CODE + "00";
            String numericIban = convertLettersToNumbers(tempIban);

            int mod97 = computeMod97(numericIban);
            String checkDigits = String.format("%02d", 98 - mod97);

            iban = COUNTRY_CODE + checkDigits + BANK_CODE + accountNumber;
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
        StringBuilder temp = new StringBuilder();
        int remainder = 0;

        for (char c : numericIban.toCharArray()) {
            temp.append(c);
            if (temp.length() >= 9) {
                remainder = Integer.parseInt(remainder + temp.toString()) % 97;
                temp.setLength(0);
            }
        }

        if (temp.length() > 0) {
            remainder = Integer.parseInt(remainder + temp.toString()) % 97;
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

