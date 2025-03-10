package com.sky.service.impl;

import com.sky.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sky.mapper.ShopMapper;

@Service
@Slf4j
public class ShopServiceImpl implements ShopService {
    
    @Autowired
    private ShopMapper shopMapper;

    /**
     * 设置店铺营业状态
     * @param status 状态值，1表示营业，0表示打烊
     */
    public void setStatus(Integer status) {
        shopMapper.updateStatus(status);
    }

    /**
     * 获取店铺营业状态
     * @return 状态值，1表示营业，0表示打烊
     */
    public Integer getStatus() {
        return shopMapper.getStatus();
    }
} 