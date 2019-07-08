package org.secutity.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: secutity-demo
 * @ClassName: SysRole
 * @description:
 * @author: AlanMa
 * @create: 2019-06-18 10:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysRole {

    private Integer roleId;
    private String roleName;
    private String roleDesc;
}