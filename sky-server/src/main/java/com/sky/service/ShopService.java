package com.sky.service;

/**
 * 店铺相关服务
 */
public interface ShopService {

    /**
     * 设置店铺营业状态
     * @param status 状态值，1表示营业，0表示打烊
     */
    void setStatus(Integer status);

    /**
     * 获取店铺营业状态
     * @return 状态值，1表示营业，0表示打烊
     */
    Integer getStatus();
} 