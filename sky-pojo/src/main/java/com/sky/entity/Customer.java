package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer implements Serializable {
    // 序列化ID，用于对象序列化时的身份标识
    private static final long serialVersionUID = 1L;

    // 主键ID
    private Long id;

    // 用户ID，关联用户信息
    private Long userId;

    // 客户名称
    private String customerName;

    // 工作编号
    private String workNumber;

    // 在线状态，0表示在线，1表示离线，2表示忙碌
    private Integer onlineStatus;

    // 会话数量
    private Integer conversations;
}