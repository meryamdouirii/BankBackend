package nl.inholland.mysecondapi.security;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import nl.inholland.mysecondapi.models.enums.UserRole;
import nl.inholland.mysecondapi.services.UserDetailsServiceJpa;
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
    private final String SECRET_KEY = "bWVnYXRocm9uLWJyb3duaWUtdG90YWwtY2FsaXR5IQ==";
    SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    private final UserDetailsServiceJpa userDetailsServiceJpa;


    public JwtProvider(UserDetailsServiceJpa userDetailsServiceJpa) {
        this.userDetailsServiceJpa = userDetailsServiceJpa;
    }




    public String createToken (String username, UserRole role) {
        Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
        return Jwts.builder()
                .subject(username)
                .claim("auth", role.name())
                .issuedAt(new Date())
                .expiration(expiration)
                .signWith(key)
                .compact();


    }
    // add method to create JWT
// add method to verify JWT
    public Authentication getAuthentication(String token) { // EIG MET TRY CATCH
        Claims claims = Jwts.parser().verifyWith(key).build().parseClaimsJws(token).getPayload();
        String username = claims.getSubject();
        UserDetails userDetails= userDetailsServiceJpa.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }


}

