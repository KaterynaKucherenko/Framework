package com.mjc.school.service.services;

import com.mjc.school.repository.model.UserModel;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.Serializable;
import java.util.Date;
import java.util.function.Function;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenService implements Serializable {
    public static final long JWT_TOKEN_VALIDITY = 1 * 60 * 60;
    private final String SECRET_KEY = "secret";


    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token, Claims::getSubject);
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private Date getExpirationDateFromToken(String token) {

        return getClaimsFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimsFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof UserModel userModelModelDetails) {
            claims.put("id", userModelModelDetails.getId());
            claims.put("role", userModelModelDetails.getRole());
        }
        String token = generateToken(claims, userDetails.getUsername());
        System.out.println("Generated Token: " + token);
        return token;
    }

    private String generateToken(Map<String, Object> claims, String subject) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_TOKEN_VALIDITY * 1000);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}





