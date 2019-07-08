package org.secutity.utils;

/**
 * @program: secutity-demo
 * @ClassName: GetClassAndMethodName
 * @description:
 * @author: AlanMa
 * @create: 2019-06-20 11:11
 */
public class GetClassAndMethodName {

    public static String getClassName() {
        String clazzName = new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(0, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
        return clazzName;
    }

    public static String getMethodName() {
        String funcName = new Throwable().getStackTrace()[1].getMethodName();
        return funcName;
    }

    public static void getClassAndMethodName() {
        System.out.println("################ "+getClassName() + "-----" + getMethodName()+"##############");
    }
}