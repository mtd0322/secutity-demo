package org.secutity.web;


import org.secutity.annotation.DebugLogger;
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
@RequestMapping("/user")
public class SysUserController {

    @Autowired
    private SysUserService SysUserService;

    @RequestMapping("/getUserInfo")
    @DebugLogger(detail = "SysUserController")
    public SysUser getUserInfo(@RequestParam(value = "info") String info){
        return SysUserService.getSysUser(info);
    }

    @RequestMapping("/login")
    @DebugLogger(detail = "SysUserController")
    public String getUserInfo(@RequestParam(value = "username") String username,@RequestParam(value = "password") String password){
        return username + password;
    }
}