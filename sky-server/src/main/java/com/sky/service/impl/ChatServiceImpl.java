package com.sky.service.impl;


import com.sky.entity.Chat;
import com.sky.mapper.ChatMapper;
import com.sky.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatMapper chatMapper;
    /**
     * 保存聊天记录
     * @param chat 聊天记录对象
     */
    public void saveChat(Chat chat) {
        chatMapper.insert(chat);
    }
    /**
     * 获取指定订单的聊天记录
     * @param orderId 订单ID
     * @return 聊天记录列表
     */
    public List<Chat> getChatsByOrderId(Long orderId) {
        return chatMapper.selectByOrderId(orderId);
    }
    /**
     * 将消息标记为已读
     * @param chatId 聊天记录ID
     */
    public void markAsRead(Long chatId) {
        chatMapper.updateReadStatus(chatId, 0);
    }
    /**
     * 获取未读消息数量
     * @param orderId 订单ID
     * @param role 角色（0用户 1客服）
     * @return 未读消息数量
     */
    public int getUnreadCount(Long orderId, Integer role) {
        return chatMapper.countUnread(orderId, role);
    }
} 