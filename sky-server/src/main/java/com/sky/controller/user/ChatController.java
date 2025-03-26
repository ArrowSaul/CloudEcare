package com.sky.controller.user;
import com.sky.entity.Chat;
import com.sky.result.Result;
import com.sky.service.ChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/chat")
@Api(tags = "C端-聊天记录相关接口")
@Slf4j
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * 获取订单的聊天记录
     */
    @GetMapping("/history/{orderId}")
    @ApiOperation("获取订单的聊天记录")
    public Result<List<Chat>> getChatHistory(@PathVariable Long orderId) {
        List<Chat> chatList = chatService.getChatsByOrderId(orderId);
        return Result.success(chatList);
    }

    /**
     * 标记消息为已读
     */
    @PutMapping("/read/{chatId}")
    @ApiOperation("标记消息为已读")
    public Result<String> markAsRead(@PathVariable Long chatId) {
        chatService.markAsRead(chatId);
        return Result.success();
    }

    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread")
    @ApiOperation("获取未读消息数量")
    public Result<Integer> getUnreadCount(@RequestParam Long orderId, @RequestParam Integer role) {
        int count = chatService.getUnreadCount(orderId, role);
        return Result.success(count);
    }
} 