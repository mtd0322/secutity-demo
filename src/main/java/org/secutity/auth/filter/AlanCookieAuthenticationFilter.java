package org.secutity.auth.filter;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.secutity.auth.token.generator.TokenGenerator;
import org.secutity.utils.GetClassAndMethodName;
import org.secutity.web.model.SysUser;
import org.secutity.web.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 安全认证过滤器
 * 认证原理:认证服务器会在客户端和服务器之间进行拦截,将用户编号写入cookie中,并删除原有cookie保证其有效
 * 过滤器工作原理:
 * A 登录接口请求: 从cookie中拿到用户编号,去数据库验证是否有这个人,将username和password放入request中,交给后续过滤器进行登录.
 * B 普通接口请求: 从cookie中拿到用户编号,去数据库验证是否有这个人,创建token,将token放到request中.交给后续过滤器.
 * 如果没有cookie 就放过,继续用原有方式进行.
 * @Author AlanMa
 **/
public class AlanCookieAuthenticationFilter extends BasicHttpAuthenticationFilter {
    /** 日志 */
    private static final Logger logger = LoggerFactory.getLogger(AlanCookieAuthenticationFilter.class);

    /** 请求响应 token 对应 Header 名称 */
    public static final String AUTHENTICATION_HEADER_NAME = "Authorization";
    /** 用户名参数默认名称 */
    public static final String DEFAULT_USERNAME_PARAM = "username";
    /** 密码参数默认名称 */
    public static final String DEFAULT_PASSWORD_PARAM = "password";

    /** 请求中的cookie 这个有安全系统拦截并添加到cookie中 */
    public static final String AUTHENTICATION_COOKIE_NAME = "KOAL_CERT_CN";
    /** token生成器 */
    private TokenGenerator tokenGenerator;

    /** 构造方法 */
    public AlanCookieAuthenticationFilter(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator;
    }

    /**
     * 只对cookie和request进行操作 全部都返回true
     * @param request     request
     * @param response    response
     * @param mappedValue mappedValue
     * @return 全都是true
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        //1 从cookie中拿东西
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        String koalId = getCookie(httpServletRequest, AUTHENTICATION_COOKIE_NAME);
        logger.info("Cookie过滤器,读取cookie.{}={}", AUTHENTICATION_COOKIE_NAME, koalId);
        if (koalId == null || koalId.length() == 0) {
            return true;
        }

        //2 用koalId去数据库读取用户信息
        //判断一下用户是否存在
        SysUser sysUser = null;
        try {
            //不太清楚和安全登录提供方商量的那个字段,都试一试先,以后确定了再整理.
            sysUser = sysUser != null ? sysUser : SysUserService.SELF.getUserByOneField("u_idcard", koalId);
            if (sysUser == null) {
                throw new Exception("无用户");
            }
        } catch (Exception e) {
            logger.error("Cookie过滤器,cookie.{}={}, 没有从本地数据库中读取到对应人员!", AUTHENTICATION_COOKIE_NAME, koalId);
            return true;
        }
        String username = sysUser.getUsername();
        String password = sysUser.getPassword();

        //3 写入用户信息到request
        if ("/login".equals(httpServletRequest.getServletPath())) {
            //3.1 如果是登录请求
            setParameter(request, DEFAULT_USERNAME_PARAM, username);
            setParameter(request, DEFAULT_PASSWORD_PARAM, password);
            logger.info("Cookie过滤器,用户{}登录请求");
        } else {
            //3.2 拼装token
            AuthenticationToken token = createToken(username, password, request, response);
            String jwtToken = tokenGenerator.generateToken((String) token.getPrincipal());
            setParameter(request, AUTHENTICATION_HEADER_NAME, jwtToken);
            logger.info("Cookie过滤器,用户{}生成token={}", koalId, jwtToken);
        }
        return true;
    }

    /**
     * 内部方法 读取cookie的值
     * @param httpServletRequest 请求
     * @param cookieName         名称
     * @return 值
     */
    private String getCookie(HttpServletRequest httpServletRequest, String cookieName) {
        String koalId = null;
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    koalId = cookie.getValue();
                    break;
                }
            }
        }
        return koalId;
    }

    /**
     * 给request增加参数
     * @param request 请求
     * @param name    键
     * @param value   值
     */
    @SuppressWarnings("unchecked")
    private void setParameter(ServletRequest request, String name, String value) {
        //先随便取一下 会把url中的参数都写入到paramHashValues中 要不然就丢了 有没有更好方式待查
        request.getParameter("test");
        //将值写入request中,因为request中的parameters禁止写入,所以使用反射方法进行强行写入.
        //个人感觉这种方法耦合更小,复杂内容更局部.
        try {
            Field innerRequestFacadeField = request.getClass().getSuperclass().getSuperclass().getDeclaredField("request");
            innerRequestFacadeField.setAccessible(true);
            Object innerRequestFacade = innerRequestFacadeField.get(request);//获取到request对象

            Field innerRequestField = innerRequestFacade.getClass().getDeclaredField("request");
            innerRequestField.setAccessible(true);
            Object innerRequest = innerRequestField.get(innerRequestFacade);

            Field coyoteRequestField = innerRequest.getClass().getDeclaredField("coyoteRequest");
            coyoteRequestField.setAccessible(true);
            Object coyoteRequest = coyoteRequestField.get(innerRequest);//获取到coyoteRequest对象

            Field parametersField = coyoteRequest.getClass().getDeclaredField("parameters");
            parametersField.setAccessible(true);
            Object parameters = parametersField.get(coyoteRequest);//获取到parameter的对象

            //获取hashtable来完成对参数变量的修改
            Field paramHashValuesField = parameters.getClass().getDeclaredField("paramHashValues");
            paramHashValuesField.setAccessible(true);
            Map<String, List<String>> map = (Map<String, List<String>>) paramHashValuesField.get(parameters);
            map.put(name, new ArrayList<>(Arrays.asList(value)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
