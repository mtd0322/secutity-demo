package org.secutity.web.model;

import com.alibaba.fastjson.annotation.JSONType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @program: learn-more
 * @EnumName: ResultEnum
 * @description: 返回值错误码定义
 * @author: AlanMa
 * @create: 2019-04-10 18:17
 */
@AllArgsConstructor
@Getter
@JSONType(serializeEnumAsJavaBean = true)
public enum ResultEnum {

    /**
     * @Author AlanMa
     * @Description 系统返回值定义
     * @Date 2019/4/23
     */
    SYSTEM_FAILED(-1, "失败"),
    SYSTEM_SUCCESS(0, "成功"),
    DATA_NOT(1, "数据不存在"),
    PARAMS_ERR(2, "参数错误"),
    UN_AUTH(50, "未认证")
    ;

    private Integer code;
    private String msg;
}
