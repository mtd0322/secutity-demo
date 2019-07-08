package org.secutity.utils;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.secutity.web.model.SysUser;

/**
 * @program: learn-more
 * @ClassName: GetSysUserUtils
 * @description: 工具类
 * @author: AlanMa
 * @create: 2019-04-16 18:05
 */
public class GetSysUserUtils {

    /**
     * 获取当前登录的用户，若用户未登录，则返回未登录 json
     *
     * @return
     */
    public static SysUser getCurrentLoginUser() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            Object principal = subject.getPrincipals().getPrimaryPrincipal();
            if (principal instanceof SysUser) {
                return (SysUser) principal;
            }
        }
        return null;
    }
}