package org.secutity.config;

import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SessionStorageEvaluator;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSessionStorageEvaluator;
import org.secutity.auth.filter.AlanAuthenticationFilter;
import org.secutity.auth.filter.AlanCookieAuthenticationFilter;
import org.secutity.auth.filter.AlanRolesAuthorizationFilter;
import org.secutity.auth.realm.AlanRealm;
import org.secutity.auth.realm.RedisJwtTokenValidateRealm;
import org.secutity.auth.token.generator.JwtTokenGenerator;
import org.secutity.auth.token.storage.RedisJwtTokenStorage;
import org.secutity.auth.token.validator.RealmJwtTokenValidator;
import org.secutity.auth.token.validator.TokenValidator;
import org.secutity.web.LogService;
import org.secutity.web.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Alan
 * @date 2018/7/3 15:36
 */
@ConditionalOnProperty(name = "shiro.enabled", matchIfMissing = true)
@Configuration
public class ShiroConfiguration {

    public static final String LOGIN_URL = "/login";

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Value("${security.authentication.token.expireMinutes:30}")
    private Long tokenExpireMinutes;
    @Autowired
    private LogService logService;

    @Bean
    @ConditionalOnProperty(name = "security.authentication.token.storage", havingValue = "redis")
    public RedisJwtTokenStorage redisJwtTokenStorage() {
        return new RedisJwtTokenStorage(redisTemplate, tokenExpireMinutes);
    }

    @Bean
    @ConditionalOnBean(name = "redisJwtTokenStorage")
    public JwtTokenGenerator jwtTokenGenerator(RedisJwtTokenStorage redisJwtTokenStorage) {
        return new JwtTokenGenerator(redisJwtTokenStorage);
    }

    @Bean
    public TokenValidator tokenValidator() {
        return new RealmJwtTokenValidator();
    }

    @Bean
    public Realm alanRealm() {
        return new AlanRealm();
    }

    @Bean
    @ConditionalOnBean(name = "redisJwtTokenStorage")
    public Realm redisJwtTokenValidateRealm(SysUserService userService, RedisJwtTokenStorage redisJwtTokenStorage) {
        return new RedisJwtTokenValidateRealm(userService, redisJwtTokenStorage);
    }

    /**
     * 全局禁用session
     * @return
     */
    @Bean
    public SessionStorageEvaluator sessionStorageEvaluator() {
        DefaultWebSessionStorageEvaluator sessionStorageEvaluator = new DefaultWebSessionStorageEvaluator();
        sessionStorageEvaluator.setSessionStorageEnabled(false);
        return sessionStorageEvaluator;
    }

    /**
     * Filter 注册
     * @return  Shiro Filter配置对应的LinkedHashMap。key值为Filter的名称，value为Filter对应的实例
     */
    private Map<String, Filter> registerUserFilter(JwtTokenGenerator jwtTokenGenerator, TokenValidator tokenValidator, LogService logService) {
        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("cookie", new AlanCookieAuthenticationFilter(jwtTokenGenerator));
        filters.put("token", new AlanAuthenticationFilter(jwtTokenGenerator, tokenValidator, logService));
        filters.put("roles", new AlanRolesAuthorizationFilter());
        return filters;
    }

    /**
     * 注册 FilterChain
     * @return  {@link ShiroFilterChainDefinition}
     */
    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        chainDefinition.addPathDefinition("/publish/audit/**", "cookie, token, roles[admin]");
        chainDefinition.addPathDefinition("/**", "cookie, token");
        return chainDefinition;
    }

    /**
     * ShiroFilterFactoryBean配置
     * @param securityManager SecurityManager实例，shiro-spring-boot-web-starter配置好了这个bean，这里可以直接注入
     * @param shiroFilterChainDefinition ShiroFilterChainDefinition实例，这个类用来注册FilterChain
     * @return  {@link ShiroFilterFactoryBean}
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager,
                                                         ShiroFilterChainDefinition shiroFilterChainDefinition,
                                                         JwtTokenGenerator jwtTokenGenerator,
                                                         TokenValidator tokenValidator) {
        ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();
        filterFactoryBean.setSecurityManager(securityManager);
        // 配置全局的loginUrl，这样不用单独为每一个Filter设置loginUrl属性了
        filterFactoryBean.setLoginUrl(LOGIN_URL);
        filterFactoryBean.setFilters(registerUserFilter(jwtTokenGenerator, tokenValidator, logService));
//        filterFactoryBean.setFilters(registerUserFilter());
        //拦截器链的配置
        filterFactoryBean.setFilterChainDefinitionMap(shiroFilterChainDefinition.getFilterChainMap());
        return filterFactoryBean;
    }

    /**
     * 禁用 RememberMe ，否则 Shiro 会向cookie中设置 rememberme
     * 因为shiro-spring-boot-web-starter中默认会配置一个RememberMeManager实例的Bean，这里配置一个空实现禁止其自动配置
     * @return
     */
    @Bean
    public RememberMeManager rememberMeManager() {
        return null;
    }

}
