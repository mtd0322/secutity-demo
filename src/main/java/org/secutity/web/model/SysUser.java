package org.secutity.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: secutity-demo
 * @ClassName: SysUser
 * @description:
 * @author: AlanMa
 * @create: 2019-06-18 10:16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysUser {

    private String username;
    private String password;
    private String createTime;
    private String info;
}