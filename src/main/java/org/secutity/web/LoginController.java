package org.secutity.web;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.secutity.annotation.DebugLogger;
import org.secutity.utils.GetClassAndMethodName;
import org.secutity.utils.ResultDataUtil;
import org.secutity.web.model.ResultData;
import org.secutity.web.model.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: secutity-demo
 * @ClassName: SysUserController
 * @description:
 * @author: AlanMa
 * @create: 2019-06-18 10:17
 */
@RestController
public class LoginController {

    @Autowired
    private SysUserService SysUserService;

    @RequestMapping("/login")
    @DebugLogger(detail = "LoginController")
    public ResultData getUserInfo(@RequestParam(value = "userName") String userName, @RequestParam(value = "password") String password){
        GetClassAndMethodName.getClassAndMethodName();

        try {
            UsernamePasswordToken token = new UsernamePasswordToken(userName, password);
            Subject subject = SecurityUtils.getSubject();
            subject.login(token);
            SysUser user = (SysUser) subject.getPrincipal();
            return ResultDataUtil.setSuccessResult(user);
        } catch (DisabledAccountException e) {
            return ResultDataUtil.setSuccessResult("账户已被禁用");
        } catch (AuthenticationException e) {
            return ResultDataUtil.setSuccessResult("用户名或密码错误");
        }
    }
}