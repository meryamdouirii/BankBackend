package nl.inholland.mysecondapi.models.enums;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    ROLE_EMPLOYEE, ROLE_ADMINISTRATOR, ROLE_CUSTOMER;

    public String getAuthority() {
        return name();
    }

    }
