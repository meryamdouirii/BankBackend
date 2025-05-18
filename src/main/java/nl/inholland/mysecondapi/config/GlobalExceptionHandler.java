package nl.inholland.mysecondapi.config;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle all exceptions except security-related ones
    @ExceptionHandler(Exception.class)
    public void handleAllExceptions(
            Exception ex,
            HttpServletResponse response
    ) throws Exception {
        // Check if the exception is one we want to exclude
        if (ex instanceof AccessDeniedException || ex instanceof AuthenticationException) {
            throw ex; // Re-throw to let Spring Security handle it
        }

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write("Internal Server Error: " + ex.getMessage());
    }
}