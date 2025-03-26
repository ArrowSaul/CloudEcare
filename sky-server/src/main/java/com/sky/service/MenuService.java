package com.sky.service;



import com.sky.entity.Menu;

import java.util.List;

public interface MenuService {
    /**
     * 查询菜单
     * @return
     */
    List<Menu> list();
}
