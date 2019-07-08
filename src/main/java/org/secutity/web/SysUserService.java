package org.secutity.web;

import org.secutity.web.model.SysMenu;
import org.secutity.web.model.SysRole;
import org.secutity.web.model.SysUser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: secutity-demo
 * @ClassName: SysUserService
 * @description:
 * @author: AlanMa
 * @create: 2019-06-18 10:17
 */
@Service
public class SysUserService {
    public static SysUserService SELF;

    public SysUser getSysUser(String str) {
        SysUser sysUser = new SysUser(){{
            setCreateTime("2019-06-18");
            setUsername("aaaa");
        }};
        sysUser.setInfo(str);
        return sysUser;
    }
    public SysUser getUserByUsername(String userName){
        SysUser sysUser = new SysUser(){{
            setCreateTime("2019-06-18");
            setUsername("aaaa");
            setPassword("2222");
            setInfo("des");
        }};
        return sysUser;
    }
    public List<SysRole> getUserRolesByUsername(String userName) {
        List<SysRole> sysRoleList = new ArrayList<>();
        SysRole sysRole = new SysRole(){{
            setRoleDesc("haha");
            setRoleId(1);
            setRoleName("user");
        }};
        sysRoleList.add(sysRole);
        return sysRoleList;
    }

    public List<SysMenu> getUserMenuByUsername(String userName){
        List<SysMenu> sysMenuList = new ArrayList<>();
        SysMenu sysMenu = new SysMenu(){{
            setMenuId("1111");
            setName("menu");
            setUrl("http://www.baidu.com");
        }};
        sysMenuList.add(sysMenu);
        return sysMenuList;
    }
    public SysUser getUserByOneField(String fieldName, String fieldValue) {
        SysUser sysUser = new SysUser(){{
            setCreateTime("2019-06-18");
            setUsername("aaaa");
            setInfo("des");
        }};
        return sysUser;
    }
}