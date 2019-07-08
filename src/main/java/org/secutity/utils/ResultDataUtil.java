package org.secutity.utils;

import org.secutity.web.model.ResultData;
import org.secutity.web.model.ResultEnum;

/**
 * @program: learn-more
 * @ClassName: ResultDataUtil
 * @description: 返回值工具类
 * @author: AlanMa
 * @create: 2019-04-10 18:14
 */
public class ResultDataUtil {

    /**
     * @Author AlanMa
     * @Description
     * @Date 2019/4/25
     * @Param [code, msg, data]
     * @return org.alan.commons.model.ResultData
     */
    public static<T> ResultData setResultData(Integer code, String msg, T data) {
        return new ResultData(code,msg,data);
    }

    public static ResultData setResultData(Integer code, String msg) {
        return new ResultData(code,msg,null);
    }
    public static ResultData setErrorResult(ResultEnum e) {
        return new ResultData(e.getCode(),e.getMsg(),null);
    }
    public static<T> ResultData setSuccessResult(T data) {
        return new ResultData(ResultEnum.SYSTEM_SUCCESS.getCode(),ResultEnum.SYSTEM_SUCCESS.getMsg(),data);
    }
    public static ResultData setSuccessResult() {
        return new ResultData(ResultEnum.SYSTEM_SUCCESS.getCode(),ResultEnum.SYSTEM_SUCCESS.getMsg(),null);
    }
}