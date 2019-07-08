package org.secutity.auth.realm;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.secutity.auth.JwtAuthenticationToken;
import org.secutity.auth.token.generator.JwtTokenGenerator;
import org.secutity.auth.token.storage.RedisJwtTokenStorage;
import org.secutity.web.SysUserService;
import org.secutity.web.model.SysMenu;
import org.secutity.web.model.SysRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author AlanMa
 */
public class RedisJwtTokenValidateRealm extends AuthorizingRealm {

    private static final Logger logger = LoggerFactory.getLogger(RedisJwtTokenValidateRealm.class);
    /**
     * @see SysUserService
     */
    private SysUserService sysUserService;
    /**
     * @see RedisJwtTokenStorage
     */
    @Autowired
    private RedisJwtTokenStorage redisJwtTokenStorage;

    @Autowired
    public RedisJwtTokenValidateRealm(SysUserService sysUserService, RedisJwtTokenStorage redisJwtTokenStorage) {
        this.sysUserService = sysUserService;
        this.redisJwtTokenStorage = redisJwtTokenStorage;
    }

    @Override
    public Class getAuthenticationTokenClass() {
        return JwtAuthenticationToken.class;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) token;
        String jwt = (String) jwtToken.getPrincipal();
        // 验证 token 有效性，确保其未被恶意修改
        try {
            Jwts.parser()
                    .setSigningKey(JwtTokenGenerator.SECURITY_KEY.getBytes())
                    .parseClaimsJws(jwt);
        } catch (SignatureException e) {
            throw new AuthenticationException("Token验证失败", e);
        }

        if (!redisJwtTokenStorage.exists(jwt)) {
            logger.debug("从Redis中读取Token失败！");
            throw new AuthenticationException("无效Token");
        }

        logger.debug("重置Token[{}]剩余有效期 {}", jwt, redisJwtTokenStorage.getTokenTTL(jwt));
        // token 验证通过，重置 token 有效时间
        redisJwtTokenStorage.resetExpireTime(jwt);
        logger.debug("重置Token[{}]有效时间为 {}", jwt, redisJwtTokenStorage.getTokenTTL(jwt));

        String username = Jwts.parser()
                .setSigningKey(JwtTokenGenerator.SECURITY_KEY.getBytes())
                .parseClaimsJws(jwt).getBody().getSubject();
        return new SimpleAuthenticationInfo(username, Boolean.TRUE, getName());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        List<SysRole> userRoles = sysUserService.getUserRolesByUsername((String)principals.getPrimaryPrincipal());
        if (userRoles == null || userRoles.isEmpty()) {
            return null;
        }

        Set<String> roles = userRoles.stream().map(SysRole::getRoleName).collect(Collectors.toSet());

        //获取权限
        List<SysMenu> menuList = sysUserService.getUserMenuByUsername((String)principals.getPrimaryPrincipal());
        if (menuList == null || menuList.isEmpty()) {
            return null;
        }

        Set<String> menus = menuList.stream().map(SysMenu::getMenuId).collect(Collectors.toSet());
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //设置权限
        info.setStringPermissions(menus);
        //设置角色
        info.setRoles(roles);
        return info;
    }
}
