package nl.inholland.mysecondapi.models.enums;

import lombok.Getter;

@Getter
public enum AmountFilterType {
    GREATER(0),
    LESS(1),
    EQUAL(2);

    private final int code;
    AmountFilterType(int code) { this.code = code; }
}
