package org.secutity.auth.token.generator;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.secutity.auth.token.storage.TokenStorage;

import java.util.Date;

/**
 * @Author AlanMa
 */
public class JwtTokenGenerator implements TokenGenerator {

    public static final String SECURITY_KEY = "I don't known how to get password";

    /**
     * token 颁发者
     */
    private String issuer = "alan";
    /**
     * 加密算法
     */
    private SignatureAlgorithm algorithm;
    /**
     * 保存 token
     */
    private TokenStorage tokenStorage;

    public JwtTokenGenerator() {
        this("AlanMa", SignatureAlgorithm.HS512, null);
    }

    public JwtTokenGenerator(TokenStorage tokenStorage) {
        this("AlanMa", SignatureAlgorithm.HS512, tokenStorage);
    }

    public JwtTokenGenerator(String issuer, SignatureAlgorithm algorithm, TokenStorage tokenStorage) {
        this.issuer = issuer;
        this.algorithm = algorithm;
        this.tokenStorage = tokenStorage;
    }

    @Override
    public String generateToken(String principal, Object... params) {
        long currentTimeMillis = System.currentTimeMillis();
        String token = Jwts.builder()
                .setSubject(principal)
                .setIssuedAt(new Date(currentTimeMillis))
                .setIssuer(issuer)
                // 如果设置了JWT的过期时间，那这个token过期时间就是固定的了，借助Redis或其他方式保存并验证token
                // .setExpiration(new Date(currentTimeMillis + TimeUnit.HOURS.toMillis(3)))
                .signWith(algorithm, SECURITY_KEY.getBytes())
                .compact();

        // 保存 token，在 token 验证的时候需要获取
        if (tokenStorage != null) {
            tokenStorage.saveToken(token);
        }
        return token;
    }
}
