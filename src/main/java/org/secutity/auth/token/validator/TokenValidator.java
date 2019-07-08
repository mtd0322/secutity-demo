package org.secutity.auth.token.validator;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @Author AlanMa
 */
public interface TokenValidator {

    /**
     * 验证token有效性
     * @param request   {@link ServletRequest}
     * @param response  {@link ServletResponse}
     * @param token     Jwt Token
     * @return          true:token有效；false:token无效
     */
    boolean validateToken(ServletRequest request, ServletResponse response, String token);
}
