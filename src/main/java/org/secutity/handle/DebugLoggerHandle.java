package org.secutity.handle;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.secutity.annotation.DebugLogger;
import org.secutity.utils.GetSysUserUtils;
import org.secutity.utils.HttpContextUtils;
import org.secutity.utils.IpUtil;
import org.secutity.web.model.SysUser;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @program: secutity-demo
 * @ClassName: DebugLoggerHandle
 * @description:
 * @author: AlanMa
 * @create: 2019-06-20 11:42
 */
@Aspect
@Component
public class DebugLoggerHandle {
    @Pointcut("@annotation(org.secutity.annotation.DebugLogger)")
    public void pointcut() {

    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) {
        Object result = null;
        long beginTime = System.currentTimeMillis();
        try {
            result = point.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        long time = System.currentTimeMillis() - beginTime;
        System.out.println("运行时间：" + time);
        saveLog(point,time);
        return result;
    }

    private void saveLog(ProceedingJoinPoint joinPoint, long time) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DebugLogger logAnnotation = method.getAnnotation(DebugLogger.class);

        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        Object[] args = joinPoint.getArgs();
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] paramNames = u.getParameterNames(method);
        String params = "";
        if (args != null && paramNames != null) {
            for (int i = 0; i < args.length; i++) {
                params += " " + paramNames[i] + ": " + args[i];
            }
        }
        //获取request
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        SysUser sysUser = GetSysUserUtils.getCurrentLoginUser();
        String mail = "无";
        if (Objects.nonNull(sysUser)){
            mail = sysUser.getUsername();
        }

        if (logAnnotation != null) {
            System.out.println("####################################################");
            System.out.println("IP：" + IpUtil.getClientAddress(request));
            System.out.println("用户名：" + mail);
            System.out.println("类名称：" + className);
            System.out.println("方法名：" + methodName);
            System.out.println("参数：" + params);
            System.out.println("运行时间：" + time);
            System.out.println("描述信息：" + logAnnotation.detail());
            System.out.println("####################################################");
        }
    }
}