package org.secutity.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.secutity.web.model.Result;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author AlanMa
 */
public class AlanRolesAuthorizationFilter extends RolesAuthorizationFilter {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        Subject subject = getSubject(request, response);
        // If the subject isn't identified, redirect to login URL
        if (subject.getPrincipal() == null) {
            WebUtils.toHttp(response).setStatus(HttpStatus.UNAUTHORIZED.value());
            sendResult(response, Result.build().unauthenticated());
        } else {
            WebUtils.toHttp(response).setStatus(HttpStatus.UNAUTHORIZED.value());
            sendResult(response, Result.build().unauthorized("用户权限不足", null));
        }
        return false;
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
}
