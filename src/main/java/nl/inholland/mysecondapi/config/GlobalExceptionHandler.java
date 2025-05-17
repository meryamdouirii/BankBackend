package nl.inholland.mysecondapi.config;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Explicitly exclude security-related exceptions
    @ExceptionHandler({
            NullPointerException.class,
            IllegalArgumentException.class,
            // Add other business exceptions
    })
    public void handleBusinessExceptions(
            Exception ex,
            HttpServletResponse response
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write("Internal Server Error: " + ex.getMessage());
    }
}