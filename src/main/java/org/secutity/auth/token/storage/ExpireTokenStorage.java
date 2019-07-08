package org.secutity.auth.token.storage;

/**
 * @Author AlanMa
 */
public abstract class ExpireTokenStorage implements TokenStorage {

    /**
     * 默认 token 有效分钟数：30分钟
     */
    protected static final Long DEFAULT_EXPIRE_MINUTES = 30L;
    /**
     * toke 有效分钟数
     */
    private Long expireMinutes;

    public ExpireTokenStorage() {
        this(DEFAULT_EXPIRE_MINUTES);
    }

    public ExpireTokenStorage(Long expireMinutes) {
        this.expireMinutes = expireMinutes;
    }

    /**
     * 设置 token 有效期
     * @param expireMinutes    有效时间
     */
    public void setExpireMinutes(Long expireMinutes) {
        this.expireMinutes = expireMinutes;
    }

    public Long getExpireMinutes() {
        return expireMinutes;
    }
}
