package com.example.demo;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    private static final String SECRET_KEY = "estaEsMiSuperLlaveSecretaCompuestaPorMuchosCaracteres";

    public static String generateToken(String email, String name) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 300_000);

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("name", name);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}
