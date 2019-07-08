package org.secutity.auth.realm;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.secutity.web.SysUserService;
import org.secutity.web.model.SysUser;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author AlanMa
 */
public class AlanRealm extends AuthorizingRealm {

    @Autowired
    private SysUserService sysUserService;
    /**
     * 在调用 {@link this#doGetAuthenticationInfo(AuthenticationToken)} 获取 {@link AuthenticationInfo} 之前
     * Shiro会先调用 {@link super#supports(AuthenticationToken)} 方法来判断该 {@link Realm} 是否支持对应类型的
     * {@link AuthenticationToken}，验证条件之一就是会判断传递过来的 {@link AuthenticationToken} 是否与该方法
     * 获取到的token类型“匹配”（该类或其子类）
     * @return  {@link org.apache.shiro.realm.AuthenticatingRealm#}
     *          默认情况下，其返回{@link UsernamePasswordToken}
     */
    @Override
    public Class getAuthenticationTokenClass() {
        return super.getAuthenticationTokenClass();
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    /**
     * 登录认证
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String) token.getPrincipal();
        SysUser sysUser = sysUserService.getUserByUsername(username);
//        Map mapDetails = new HashMap();
//
//        mapDetails.put("", "登录系统");
//        mapDetails.put("登录时间", DateUtils.dateToFormatTime(new Date()));

        if (sysUser == null) {
//            mapDetails.put("用户名", username);
//            JSONObject json = new JSONObject(mapDetails);
//            logService.addLog(new SysLogEntity() {{
//                setOperation("用户登录");
//                setDetails(json.toString());
//            }});
            throw new UnknownAccountException("用户不存在!");
        }
//        }else {
//            mapDetails.put("用户名", username);
//            mapDetails.put("用户警号", user.getPoliceId());
//            mapDetails.put("用户身份证号", user.getIdCard());
//            JSONObject json = new JSONObject(mapDetails);
//            logService.addLog(new SysLogEntity(){{
//                setOperation("用户登录");
//                setUserName(username);
//                setUserAccount(user.getRealName());
//                setPoliceId(user.getPoliceId());
//                setDetails(json.toString());
//            }});
//        }

        return new SimpleAuthenticationInfo(username, sysUser.getPassword(), getName());
    }
}
