package com.sky.service;


import com.sky.entity.Chat;

import java.util.List;

public interface ChatService {
    
    /**
     * 保存聊天记录
     * @param chat 聊天记录对象
     */
    void saveChat(Chat chat);
    
    /**
     * 获取指定订单的聊天记录
     * @param orderId 订单ID
     * @return 聊天记录列表
     */
    List<Chat> getChatsByOrderId(Long orderId);
    
    /**
     * 将消息标记为已读
     * @param chatId 聊天记录ID
     */
    void markAsRead(Long chatId);
    
    /**
     * 获取未读消息数量
     * @param orderId 订单ID
     * @param role 角色（0用户 1客服）
     * @return 未读消息数量
     */
    int getUnreadCount(Long orderId, Integer role);
} 