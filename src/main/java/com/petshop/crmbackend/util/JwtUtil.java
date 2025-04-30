package com.petshop.crmbackend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {

    // 定义一个秘钥，自己随便取一个字符串
    private static final String SECRET_KEY = "petshop_secret_key";

    // Token有效时间：比如2小时
    private static final long EXPIRATION_TIME = 2 * 60 * 60 * 1000L;

    // 生成Token
    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)  // 主题（可以放用户名）
                .setIssuedAt(new Date()) // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 过期时间
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY) // 加密算法+秘钥
                .compact();
    }

    // 解析Token
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    // 从Token中获取用户名
    public static String getUsername(String token) {
        return parseToken(token).getSubject();
    }
}
