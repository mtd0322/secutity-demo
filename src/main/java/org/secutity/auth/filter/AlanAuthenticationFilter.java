package org.secutity.auth.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.secutity.auth.token.generator.TokenGenerator;
import org.secutity.auth.token.validator.TokenValidator;
import org.secutity.web.LogService;
import org.secutity.web.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;
/**
 * @Author AlanMa
 */
public class AlanAuthenticationFilter extends AuthenticatingFilter {

    @Autowired
    private LogService logService;

    private static final Logger log = LoggerFactory.getLogger(AlanAuthenticationFilter.class);
    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    private static final String FORM_DATA_CONTENT_TYPE = "multipart/form-data";
    /**
     * 用户名参数默认名称
     */
    public static final String DEFAULT_USERNAME_PARAM = "username";
    /**
     * 密码参数默认名称
     */
    public static final String DEFAULT_PASSWORD_PARAM = "password";
    /**
     * 请求/响应 token 对应 Header 名称
     */
    public static final String AUTHENTICATION_HEADER_NAME = "Authorization";

    public static String logU = null;
    /**
     * 用户名参数名称
     */
    private String usernameParam = DEFAULT_USERNAME_PARAM;
    /**
     * 密码参数名称
     */
    private String passwordParam = DEFAULT_PASSWORD_PARAM;

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * {@link TokenGenerator}
     */
    private TokenGenerator tokenGenerator;
    /**
     * {@link TokenValidator}
     */
    private TokenValidator tokenValidator;

    public AlanAuthenticationFilter(TokenGenerator tokenGenerator,
                                    TokenValidator tokenValidator,
                                    LogService logService) {
        this.tokenGenerator = tokenGenerator;
        this.tokenValidator = tokenValidator;
        this.logService = logService;
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        // 先从requestBody中获取
        String username, password;
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        log.trace("Content-Type ===== {}", httpServletRequest.getHeader(CONTENT_TYPE_HEADER_NAME));
        if (httpServletRequest.getHeader(CONTENT_TYPE_HEADER_NAME).contains(MediaType.APPLICATION_JSON_VALUE)) {
            try (InputStreamReader inputStreamReader = new InputStreamReader(request.getInputStream());
                 BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String body = reader.lines()
                        .reduce(String::concat).orElseThrow(IllegalArgumentException::new);
                JsonNode jsonNode = objectMapper.readTree(body);
                username = jsonNode.get(usernameParam).asText();
                logU = username;
                password = jsonNode.get(passwordParam).asText();
                if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
                    return createToken(username, password, request, response);
                }
            } catch (Exception e) {
                log.info("登录拿参数没拿到,可能没提供.");
            }
        }
        // 通过表单提交的request.getParameter()获取username和password
        username = getUsername(request);
        password = getPassword(request);
        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            return createToken(username, password, request, response);
        }
        sendResult(response, Result.build()
                .fail("请设置正确的Content-Type[" + FORM_DATA_CONTENT_TYPE + "," + MediaType.APPLICATION_JSON_VALUE + "]"));
        throw new IllegalStateException("请求Content-Type错误");
    }

    protected String getUsername(ServletRequest request) {
        return WebUtils.getCleanParam(request, getUsernameParam());
    }

    protected String getPassword(ServletRequest request) {
        return WebUtils.getCleanParam(request, getPasswordParam());
    }

    protected boolean isLoginSubmission(ServletRequest request, ServletResponse response) {
        return (request instanceof HttpServletRequest) && WebUtils.toHttp(request).getMethod().equalsIgnoreCase(POST_METHOD);
    }

    private void sendResult(ServletResponse response, Result result) {
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.write(objectMapper.writeValueAsString(result));
            writer.flush();
        } catch (IOException exception) {
            throw new IllegalStateException("返回消息失败~", exception);
        }
    }

    /**
     * 判断请求是否允许通过：判断请求头中是否有token，且token未过期，验证通过绑定用户到ThreadContext中方便后续使用
     *
     * @param request     {@link ServletRequest}
     * @param response    {@link ServletResponse}
     * @param mappedValue
     * @return true：token有效；false：未携带token或无效token
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        String token = httpServletRequest.getHeader(AUTHENTICATION_HEADER_NAME);
        if (!StringUtils.hasText(token) || "null".equals(token)) {
            log.trace("Header中不存在token，继续在请求参数中查找");
            token = httpServletRequest.getParameter(AUTHENTICATION_HEADER_NAME);
            if (!StringUtils.hasText(token)) {
                log.trace("{}请求未携带token！", httpServletRequest.getRequestURI());
                // 没有携带 token 返回 false 执行 onAccessDenied() 判断是否是登录请求
                return false;
            }
        }

        // 处理前端 登录传递字符串 null
        if (isLoginRequest(request, response) && "null".equals(token)) {
            return false;
        }

        // 携带了token，进行token验证，如果token失效，将直接响应错误
        if (!validateToken(request, response, token)) {
            WebUtils.toHttp(response).setStatus(HttpStatus.UNAUTHORIZED.value());
            sendResult(response, Result.build().invalidToken());
            return false;
        }
        return true;
    }

    /**
     * 验证token，这里使用的方法是调用{@link super#executeLogin(ServletRequest, ServletResponse)}
     *
     * @param request  {@link ServletRequest}
     * @param response {@link ServletResponse}
     * @param token    前端传递过来的 token
     * @return true：表示 token 有效； false：表示 token 失效
     */
    private boolean validateToken(ServletRequest request, ServletResponse response, String token) {
        return tokenValidator.validateToken(request, response, token);
    }

    /**
     * 当{@link this#isAccessAllowed(ServletRequest, ServletResponse, Object)} 返回false时，调用该方法
     * 1. 判断是否是登录请求，如果是的话执行登录，否则返回未登录相关提示，并 return false 阻止filter继续执行；
     *
     * @param request  {@link ServletRequest}
     * @param response {@link ServletResponse}
     * @return
     * @throws {@link Exception}
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (isLoginRequest(request, response)) {
            if (isLoginSubmission(request, response)) {
                if (log.isTraceEnabled()) {
                    log.trace("Login submission detected.  Attempting to execute login.");
                }
                // 登录失败后会调用onLoginFailure()在这里处理返回逻辑
                return executeLogin(request, response);
            }
        }
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        sendResult(httpServletResponse, Result.build().unauthenticated());
        return false;
    }

    /**
     * 这里直接调用{@link super#executeLogin(ServletRequest, ServletResponse)}执行登录，即在本应用中执行登录
     * 如果有登录认证服务器的话，可以在这里执行登录接口的相关调用，并将返回的token直接设置到Response对象相应的Header中
     *
     * @param request  {@link ServletRequest}
     * @param response {@link ServletResponse}
     * @return true，登录成功；false，登录失败
     * @throws {@link Exception}
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        return super.executeLogin(request, response);
    }

    /**
     * {@link this#executeLogin(ServletRequest, ServletResponse)}登录成功后会回调这个方法，在这里可以直接将写出登录成功信息
     * 注意，如果使用response直接返回的话，需要return false防止FilterChain继续执行
     *
     * @param token    {@link AuthenticationToken}
     * @param subject  Shiro Subject对象
     * @param request  {@link ServletRequest}
     * @param response {@link ServletResponse}
     * @return 由于方法中做了响应操作，所以这里直接返回 false 禁止 FilterChain 继续执行
     * @throws {@link Exception}
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject,
                                     ServletRequest request, ServletResponse response) throws Exception {
        // 生成 token，并保存 token
        String jwtToken = tokenGenerator.generateToken((String) token.getPrincipal());
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.addHeader(AUTHENTICATION_HEADER_NAME, jwtToken);
        sendResult(httpServletResponse,
                Result.build().success("登录成功", Collections.singletonMap("token", jwtToken)));

        return false;
    }

    /**
     * {@link this#executeLogin(ServletRequest, ServletResponse)}登录失败后会回调这个方法，在这里可以直接将写出登录失败信息
     *
     * @param token    {@link AuthenticationToken}
     * @param e        {@link AuthenticationException}
     * @param request  {@link ServletRequest}
     * @param response {@link ServletResponse}
     * @return false，禁止 FilterChain 继续执行
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e,
                                     ServletRequest request, ServletResponse response) {
        log.debug("登录失败 ==> {}", e);
        WebUtils.toHttp(response).setStatus(HttpStatus.UNAUTHORIZED.value());
        sendResult(response, Result.build().loginFailed());
        log.info("登录用户" + logU);
        return super.onLoginFailure(token, e, request, response);
    }

    public String getUsernameParam() {
        return usernameParam;
    }

    public void setUsernameParam(String usernameParam) {
        this.usernameParam = usernameParam;
    }

    public String getPasswordParam() {
        return passwordParam;
    }

    public void setPasswordParam(String passwordParam) {
        this.passwordParam = passwordParam;
    }
}
