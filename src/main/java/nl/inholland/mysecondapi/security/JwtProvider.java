package nl.inholland.mysecondapi.security;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import nl.inholland.mysecondapi.models.enums.UserRole;
import nl.inholland.mysecondapi.services.UserDetailsServiceJpa;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;


@Component
public class JwtProvider {

    private final SecretKey key;
    private final UserDetailsServiceJpa userDetailsServiceJpa;

    public JwtProvider(@Value("${jwt.secret}") String secretKey, UserDetailsServiceJpa userDetailsServiceJpa) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.userDetailsServiceJpa = userDetailsServiceJpa;
    }

    public String createToken(String email, UserRole role, Long id) {
        long duration = switch (role) {
            case ROLE_ADMINISTRATOR -> 2 * 60 * 60 * 1000; // 2 hours
            case ROLE_EMPLOYEE -> 1 * 60 * 60 * 1000;       // 1 hour
            default -> 30 * 60 * 1000;                      // 30 minutes
        };

        Date now = new Date();
        Date expiration = new Date(now.getTime() + duration);

        return Jwts.builder()
                .subject(email)
                .claim("auth", role.name())
                .claim("id", id)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseClaimsJws(token)
                .getPayload();

        String email = claims.getSubject();
        Object idClaim = claims.get("id");

        if (email == null || !(idClaim instanceof Number)) {
            throw new BadCredentialsException("Invalid JWT: Missing email or ID");
        }

        Long userId = ((Number) idClaim).longValue();

        UserDetails userDetails = userDetailsServiceJpa.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        auth.setDetails(userId);
        return auth; // ✅ Return the auth object with details set
    }
}


