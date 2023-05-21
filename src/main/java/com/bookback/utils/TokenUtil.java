package com.bookback.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bookback.exception.ConditionException;

import java.util.Calendar;
import java.util.Date;

public class TokenUtil {

    private static final String ISSUER = "pluto";

    public static String generateToken(int userId) throws Exception {
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 72);
        return JWT.create().withKeyId(String.valueOf(userId)).withIssuer(ISSUER).withExpiresAt(calendar.getTime()).sign(algorithm);
    }

    public static int verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return Integer.parseInt(decodedJWT.getKeyId());
        } catch (TokenExpiredException e) {
            throw new ConditionException("token已过期");
        } catch (Exception e) {
            throw new ConditionException("非法用户token");
        }
    }
}
