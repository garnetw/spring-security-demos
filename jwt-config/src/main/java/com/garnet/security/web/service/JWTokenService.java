package com.garnet.security.web.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Calendar;
import java.util.Date;

/**
 * JWT工具类
 * 生成、解析token
 */
@Service
public class JWTokenService {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.duration}")
    private int duration;

    /**
     * 基于username生成jwt
     * @param username
     * @return token
     */
    public String generateToken(String username) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, duration);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(calendar.getTime())
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 基于token生成包含用户信息的claims
     * @param token
     * @return
     */
    public Claims parseToken(String token) {
        if(!StringUtils.hasLength(token)) {
            return null;
        }

        Claims claims = null;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new JwtException(e.getMessage());
        }
        return claims;
    }

    /**
     * 最新版，secretKey需要封装为对象
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

}
