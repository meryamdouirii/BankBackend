package nl.inholland.mysecondapi.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import nl.inholland.mysecondapi.models.User;
import nl.inholland.mysecondapi.models.enums.ApprovalStatus;
import nl.inholland.mysecondapi.models.enums.UserRole;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String bsn;
    private String email;
    private String phoneNumber;
    private BigDecimal daily_limit;
    private UserRole role;
    private boolean active;
    private ApprovalStatus approval_status;
    private List<AccountDTO> accounts;

    public UserDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.bsn = user.getBsn();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.daily_limit = user.getDaily_limit();
        this.role = user.getRole();
        this.active = user.isActive();
        this.approval_status = user.getApproval_status();

        this.accounts = user.getAccounts() != null
                ? user.getAccounts().stream()
                .map(AccountDTO::new)
                .collect(Collectors.toList())
                : null;
    }
}
