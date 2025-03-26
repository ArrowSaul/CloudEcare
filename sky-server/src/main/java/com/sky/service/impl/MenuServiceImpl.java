package com.sky.service.impl;


import com.sky.entity.Menu;
import com.sky.mapper.MenuMapper;
import com.sky.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MenuServiceImpl implements MenuService {
    @Autowired
    private MenuMapper menuMapper;
    /**
     * 查询菜单
     * @return
     */
    public List<Menu> list( ) {
        return menuMapper.list();
    }
}
