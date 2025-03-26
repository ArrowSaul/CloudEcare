package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evaluations implements Serializable {
    /**
     * 序列化版本唯一标识符
     * 用于在序列化和反序列化过程中确保类的版本一致性和兼容性
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     * 用于唯一标识每条评论记录
     */
    private Long id;

    /**
     * 用户ID
     * 表示发表评论的用户
     */
    private Long userId;

    /**
     * 订单ID
     * 关联评论所对应的订单
     */
    private Long orderId;

    /**
     * 客户ID
     * 可能与userId不同，特别是在B2B场景中，代表实际受影响的客户
     */
    private Long customerId;

    /**
     * 评分
     * 用于表示评论者给服务或产品的评分，范围是0到5，其中0表示未评分
     */
    private Integer rating; // 0未评分 1-5星

    /**
     * 评论内容
     * 存储评论的具体文本信息
     */
    private String content;

    /**
     * 创建时间
     * 记录评论创建的时间，用于追踪评论的历史和排序
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     * 记录评论最后一次更新的时间，帮助追踪评论的修改历史
     */
    private LocalDateTime updateTime;
}