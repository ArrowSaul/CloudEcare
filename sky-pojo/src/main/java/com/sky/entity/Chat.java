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
public class Chat implements Serializable {
    /**
     * 消息类
     * 用于表示和操作与消息相关的数据
     */
    private static final long serialVersionUID = 1L; // 序列化ID，用于对象序列化
    private Long id; // 消息的唯一标识符
    private Long orderId; // 关联的订单ID
    private Long customerId; // 发送者的用户ID
    private Integer sendRole; // 发送者角色 0用户 1客服
    private String content; // 消息内容
    private LocalDateTime sendTime; // 发送时间
    private Integer readStatus; // 阅读状态 0已读 1未读
}