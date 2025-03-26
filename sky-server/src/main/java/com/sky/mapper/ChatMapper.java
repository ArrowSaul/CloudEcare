package com.sky.mapper;

import com.sky.entity.Chat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMapper {
    
    /**
     * 插入聊天记录
     * @param chat 聊天记录对象
     */
    void insert(Chat chat);
    
    /**
     * 根据订单ID查询聊天记录
     * @param orderId 订单ID
     * @return 聊天记录列表
     */
    List<Chat> selectByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 更新消息读取状态
     * @param chatId 聊天记录ID
     * @param readStatus 读取状态（0已读 1未读）
     */
    void updateReadStatus(@Param("chatId") Long chatId, @Param("readStatus") Integer readStatus);
    
    /**
     * 统计未读消息数量
     * @param orderId 订单ID
     * @param role 角色（0用户 1客服）
     * @return 未读消息数量
     */
    int countUnread(@Param("orderId") Long orderId, @Param("role") Integer role);
} 