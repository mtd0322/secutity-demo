package org.secutity.auth;

import org.apache.shiro.authc.HostAuthenticationToken;

/**
 * @Author AlanMa
 */
public class JwtAuthenticationToken implements HostAuthenticationToken {

    private String token;
    private String host;

    public JwtAuthenticationToken() {
    }

    public JwtAuthenticationToken(String token, String host) {
        this.token = token;
        this.host = host;
    }

    @Override
    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return Boolean.TRUE;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "StatelessAuthenticationToken{" +
                "token='" + token + '\'' +
                ", host='" + host + '\'' +
                '}';
    }
}
