package org.secutity.utils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @program: learn-more
 * @ClassName: IpUtil
 * @description: 获取IP地址
 * @author: AlanMa
 * @create: 2019-04-18 11:16
 */
public class IpUtil {

    /**
     * 是否IP地址格式
     *
     * @param s
     * @return
     */
    public static Boolean isIpAddress(String s) {

        String regex = "(((2[0-4]\\d)|(25[0-5]))|(1\\d{2})|([1-9]\\d)|(\\d))[.](((2[0-4]\\d)|(25[0-5]))|(1\\d{2})|([1-9]\\d)|(\\d))[.](((2[0-4]\\d)|(25[0-5]))|(1\\d{2})|([1-9]\\d)|(\\d))[.](((2[0-4]\\d)|(25[0-5]))|(1\\d{2})|([1-9]\\d)|(\\d))";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(s);

        return m.matches();
    }

    /**
     * 获取客服端IP
     */
    public static String getClientAddress(HttpServletRequest request) {

        String address = request.getHeader("X-Forwarded-For");
        if (address != null && IpUtil.isIpAddress(address)) {
            return address;
        }
        address = request.getRemoteAddr();
        if ("0:0:0:0:0:0:0:1".equals(address)) {
            address = "127.0.0.1";
        }

        return address;
    }

    public static String getServerAddress(HttpServletRequest request) {

        String localServerAdd = null;
        try {
            localServerAdd = request.getScheme() + "://"
                    + InetAddress.getLocalHost().getHostAddress() + ":"
                    + request.getServerPort()
                    + request.getContextPath() + "/";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return localServerAdd;
    }
}
