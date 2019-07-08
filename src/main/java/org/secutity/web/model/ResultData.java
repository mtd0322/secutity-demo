package org.secutity.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @program: learn-more
 * @ClassName: ResultData
 * @description: 接口返回的数据
 * @author: AlanMa
 * @create: 2019-04-10 18:00
 */

@Data
@AllArgsConstructor
public class ResultData<T> {

    private Integer code;
    private String msg;
    private T data;

    @Override
    public String toString() {
        return "ResultData{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}