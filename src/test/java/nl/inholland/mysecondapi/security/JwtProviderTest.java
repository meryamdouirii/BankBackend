package nl.inholland.mysecondapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import nl.inholland.mysecondapi.models.enums.UserRole;
import nl.inholland.mysecondapi.services.UserDetailsServiceJpa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@org.junit.jupiter.api.extension.ExtendWith(MockitoExtension.class)
class JwtProviderTest {

    @Mock
    private UserDetailsServiceJpa userDetailsService;

    @Mock
    private UserDetails userDetails;

    private JwtProvider jwtProvider;
    private String secretKey = "myverysecureandlongenoughkeyformyjwt123456";

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(secretKey, userDetailsService);
    }

    @Test
    void testCreateToken_AndParseClaims() {
        String token = jwtProvider.createToken("john@example.com", UserRole.ROLE_EMPLOYEE, 42L);

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getPayload();

        assertEquals("john@example.com", claims.getSubject());
        assertEquals("ROLE_EMPLOYEE", claims.get("auth"));
        assertEquals(42, ((Number) claims.get("id")).intValue());
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    void testGetAuthentication_Success() {
        String token = jwtProvider.createToken("jane@example.com", UserRole.ROLE_CUSTOMER, 99L);
        when(userDetailsService.loadUserByUsername("jane@example.com")).thenReturn(userDetails);

        Authentication auth = jwtProvider.getAuthentication(token);

        assertNotNull(auth);
        assertEquals(userDetails, auth.getPrincipal());
        assertEquals(99L, auth.getDetails());
    }

    @Test
    void testGetAuthentication_MissingEmailOrId_ShouldThrow() {
        // Build a token with missing claims manually
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        String badToken = Jwts.builder()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(key)
                .compact();

        assertThrows(BadCredentialsException.class, () -> jwtProvider.getAuthentication(badToken));
    }
}
