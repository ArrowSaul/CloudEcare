package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Menu implements Serializable {
    /**
     * 序列化版本唯一标识符
     * 用于在序列化和反序列化过程中确保类的版本一致性和兼容性
     */
    private static final long serialVersionUID = 1L;
    private Long id; // 主键
    private String name; // 菜单名称
    private Integer sort; // 顺序
    private Integer status; // 状态 0:禁用，1:启用
    private BigDecimal price; // 金额
    private String image; // 图片路径
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
    private Long createUser; // 创建人
    private Long updateUser; // 修改人
}
