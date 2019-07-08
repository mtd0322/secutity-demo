package org.secutity.auth.token.generator;

/**
 * @Author AlanMa
 */
public interface TokenGenerator {

    /**
     * 生成token
     * @param principal 用户表示，如用户名
     * @param params    用于生成token的其他参数
     * @return          token
     */
    String generateToken(String principal, Object... params);
}
