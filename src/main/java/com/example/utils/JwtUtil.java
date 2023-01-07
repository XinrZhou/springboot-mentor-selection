package com.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.entity.User;
import com.example.exception.XException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    private static final String SING = "ADSD#$F";

    /**
     * 生成token
     * @param withClaims
     * @return
     */
    public static String generateToken(Map<String, String> withClaims) {
        // 创建Map集合
        HashMap<String, Object> map = new HashMap<>();
        // 创建日历
        Calendar instance = Calendar.getInstance();
        // 设置过期时间
        instance.add(Calendar.SECOND, 60 * 60);
        // 创建JWT
        JWTCreator.Builder builder = JWT.create();
        // 添加存放信息
        withClaims.forEach(builder::withClaim);
        // 指定令牌过期时间
        String token = builder.withExpiresAt(instance.getTime())
                // 设置签名(指定密钥方式)
                .sign(Algorithm.HMAC256(SING));
        return token;
    }

    /**
     * 验证token
     * @param token
     * @return
     */
    public static User verifyToken(String token) {
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SING)).build();
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            long uid = Long.parseLong(decodedJWT.getClaim("uid").asString());
            int role = Integer.parseInt(decodedJWT.getClaim("role").asString());

            User user = new User();
            user.setId(uid);
            user.setRole(role);

            return user;
        } catch (JWTDecodeException e) {
            throw new XException("令牌错误");
        } catch (TokenExpiredException e) {
            throw new XException("令牌过期");
        }
    }

}
