package org.secutity.auth.token.validator;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.secutity.auth.JwtAuthenticationToken;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @Author AlanMa
 */
public class RealmJwtTokenValidator implements TokenValidator {

    @Override
    public boolean validateToken(ServletRequest request, ServletResponse response, String token) {
        if (token == null || token.trim().length() == 0) {
            return false;
        }
        AuthenticationToken authenticationToken = createToken(token, request.getRemoteHost());
        try {
            Subject subject = SecurityUtils.getSubject();
            // 通过执行login操作来保存用户信息（username）到Subject中，以便可以获取到用户信息
            subject.login(authenticationToken);
            return true;
        } catch (AuthenticationException e) {
            return false;
        }
    }

    protected AuthenticationToken createToken(String token, String host) {
        return new JwtAuthenticationToken(token, host);
    }
}
