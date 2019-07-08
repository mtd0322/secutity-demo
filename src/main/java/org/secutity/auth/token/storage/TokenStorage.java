package org.secutity.auth.token.storage;

/**
 * @Author AlanMa
 */
public interface TokenStorage {
    /**
     * token 保存 key 值前缀
     */
    String TOKEN_STORAGE_KEY_PREFIX = "token_";

    /**
     * 保存 token
     * @param token token值
     */
    void saveToken(String token);

    /**
     * 判断 token 是否存在
     * @param token token值
     * @return      true：token存在；false：token不存在
     */
    boolean exists(String token);

    /**
     * 重置 token 有效时间
     * @param token token值
     * @return      token：token
     */
    boolean resetExpireTime(String token);

    /**
     * 获取 token 剩余有效时间
     * @param token token值
     * @return      token剩余有效时间
     */
    long getTokenTTL(String token);

    /**
     * 获取 token key
     * @param token token值
     * @return      token key 值
     */
    default String getTokenKey(String token) {
        return TOKEN_STORAGE_KEY_PREFIX + token;
    }

}
