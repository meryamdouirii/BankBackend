package nl.inholland.mysecondapi.config;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleAllExceptions_ReturnsInternalServerError() throws Exception {
        Exception genericException = new Exception("Something went wrong");

        ResponseEntity<String> response = handler.handleAllExceptions(genericException);

        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Something went wrong"));
    }

    @Test
    void testHandleAllExceptions_ThrowsAccessDeniedException() {
        AccessDeniedException accessDeniedException = new AccessDeniedException("Access denied");

        assertThrows(AccessDeniedException.class, () -> {
            handler.handleAllExceptions(accessDeniedException);
        });
    }

    @Test
    void testHandleAllExceptions_ThrowsAuthenticationException() {
        AuthenticationException authException = new AuthenticationException("Authentication failed") {};

        assertThrows(AuthenticationException.class, () -> {
            handler.handleAllExceptions(authException);
        });
    }
}
