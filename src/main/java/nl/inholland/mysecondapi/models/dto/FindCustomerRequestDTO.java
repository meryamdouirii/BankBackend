package nl.inholland.mysecondapi.models.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class FindCustomerRequestDTO {
    private String name;
}
