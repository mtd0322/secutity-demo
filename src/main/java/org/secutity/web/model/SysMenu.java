package org.secutity.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: secutity-demo
 * @ClassName: SysMenu
 * @description:
 * @author: AlanMa
 * @create: 2019-06-18 10:51
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysMenu {
    private String menuId;
    private String name;
    private String url;
}