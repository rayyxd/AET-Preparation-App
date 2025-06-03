package ru.rayyxd.aetpreparation.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import ru.rayyxd.aetpreparation.sqlEntities.StudentDetailsImpl;

@Component
public class JwtCore {

	@Value("${token.secret.key}")
	private String secret;
	
	@Value("${token.secret.duration}")
	private int lifetime;
	
	// Генерация JWT
    public String generateToken(Authentication authentication) {
        StudentDetailsImpl studentDetails = (StudentDetailsImpl) authentication.getPrincipal();
        
        return Jwts.builder()
                .setSubject(studentDetails.getId().toString())  // Логин как subject
                .setIssuedAt(new Date())                  // Текущая дата (iat)
                .setExpiration(new Date(System.currentTimeMillis() + lifetime))  // Срок действия
                .signWith(SignatureAlgorithm.HS256, secret)  // Алгоритм подписи
                .compact();
    }
    
    // Получение userId из JWT
    public Long getUserIdFromJwt(String token) {
        return Long.valueOf(
        		Jwts.parser()
                .setSigningKey(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject()
                );
    }
    
    // Получение exp из JWT
    public Date getExpFromJwt(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    
}
