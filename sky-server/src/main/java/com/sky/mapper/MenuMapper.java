package com.sky.mapper;


import com.sky.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface MenuMapper {
    /**
     * 查询菜单
     * @return
     */
    List<Menu> list();

    /**
     * 根据id查询菜单金额
     * @param menuId
     */
    @Select("select price from menu where id = #{menuId}")
    BigDecimal getAmountByIds(Integer menuId);
}
