package com.example.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.*;

public class JwtUtil {

    public static final Long JWT_TTL = 60 * 60 *1000L; //有效期

    public static final String JWT_KEY = "ADSD#$F";


    /**
     * 生成JWT
     * @param map
     * @return
     */
    public static String createJWT(Map<String, Object> map) {
        Date expDate = new Date(System.currentTimeMillis() + JwtUtil.JWT_TTL);
        return Jwts.builder()
                .setId(map.get("uid").toString())
                .claim("role",map.get("role"))
                .setIssuer("example")
                .signWith(SignatureAlgorithm.HS256, JwtUtil.JWT_KEY) //使用HS256对称加密算法签名
                .setExpiration(expDate)
                .compact();
    }

    /**
     * 解析JWT
     *
     * @param jwt
     * @return
     * @throws Exception
     */
    public static Claims parseJWT(String jwt) throws Exception {
        return Jwts.parser()
                .setSigningKey(JwtUtil.JWT_KEY)
                .parseClaimsJws(jwt)
                .getBody();
    }

}