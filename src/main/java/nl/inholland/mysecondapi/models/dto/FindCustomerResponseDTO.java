package nl.inholland.mysecondapi.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FindCustomerResponseDTO {
    private List<AccountInfo> accounts;  // List of accounts for the found user(s)

    @Data
    @AllArgsConstructor
    public static class AccountInfo {
        private String iban;
        private String accountType;
        private String userName;
    }
}
