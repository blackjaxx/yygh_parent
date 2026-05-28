package com.atguigu.yygh.common.util;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * JWT令牌工具类
 * 签名密钥通过系统属性或环境变量 JWT_SIGN_KEY 注入，避免硬编码
 * 默认过期时间 24 小时，可通过 JWT_EXPIRATION 环境变量(毫秒)覆盖
 */
public class JwtHelper {

    /**
     * 从系统属性/环境变量读取JWT签名密钥，默认值仅用于开发环境
     * 生产环境必须通过 -DJWT_SIGN_KEY=xxx 或环境变量 JWT_SIGN_KEY 设置强密钥
     */
    private static String tokenSignKey = System.getProperty("JWT_SIGN_KEY",
            System.getenv().getOrDefault("JWT_SIGN_KEY", "yygh-dev-default-key-change-in-production"));

    /**
     * Token过期时间，默认24小时，可通过 JWT_EXPIRATION 环境变量(毫秒)覆盖
     */
    private static long tokenExpiration = Long.parseLong(
            System.getenv().getOrDefault("JWT_EXPIRATION", String.valueOf(24 * 60 * 60 * 1000)));

    public static String createToken(Long userId, String userName) {
        String token = Jwts.builder()
                .setSubject("YYGH-USER")
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .claim("userId", userId)
                .claim("userName", userName)
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    public static Long getUserId(String token) {
        if (StringUtils.isEmpty(token)) return null;
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        // 兼容 Integer 和 Long 类型，避免 ClassCastException
        Object userIdObj = claims.get("userId");
        if (userIdObj instanceof Integer) {
            return ((Integer) userIdObj).longValue();
        } else if (userIdObj instanceof Long) {
            return (Long) userIdObj;
        }
        return null;
    }

    public static String getUserName(String token) {
        if (StringUtils.isEmpty(token)) return "";
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return (String) claims.get("userName");
    }
}
