package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ShopMapper {

    /**
     * 更新店铺营业状态
     * @param status 状态值，1表示营业，0表示打烊
     */
    @Update("update shop set status = #{status}")
    void updateStatus(Integer status);

    /**
     * 获取店铺营业状态
     * @return 状态值，1表示营业，0表示打烊
     */
    @Select("select status from shop limit 1")
    Integer getStatus();
} 