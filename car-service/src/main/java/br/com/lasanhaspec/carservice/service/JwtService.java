package br.com.lasanhaspec.carservice.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {


    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long expiration;


    //gera o token a partir od usuario autenticado
    public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();

        claims.put("role", userDetails.getAuthorities()
                .stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("ROLE_USER"));



        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }


    //extrai o emial de dentro do token
    public String extractUserName(String token){
        return extractClaim(token, Claims::getSubject);
    }


    //valida se o token pertence ao usuario e nao expirou
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUserName(token);// extrai email do token
        return username.equals(userDetails.getUsername())  && !(isTokenExpired(token));

    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }


    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }


    public <T> T extractClaim(String token,  Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }



    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return e.getClaims();
        }
    }


    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }








}
