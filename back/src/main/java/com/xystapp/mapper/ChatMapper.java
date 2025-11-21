package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.Chat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 聊天Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface ChatMapper extends BaseMapper<Chat> {

    /**
     * 获取聊天详情（包含用户信息）
     */
    Chat selectChatWithUserInfo(@Param("chatId") Long chatId, @Param("userId") Long userId);

    /**
     * 获取用户聊天列表（包含最后一条消息）
     */
    IPage<Chat> selectUserChatList(Page<Chat> page, @Param("userId") Long userId);

    /**
     * 批量获取聊天信息
     */
    List<Chat> selectChatsByIds(@Param("chatIds") List<Long> chatIds, @Param("userId") Long userId);

    /**
     * 获取活跃聊天列表
     */
    List<Chat> selectActiveChats(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 更新聊天最后消息时间
     */
    int updateLastMessageTime(@Param("chatId") Long chatId);

    /**
     * 获取私聊信息（包含对方用户信息）
     */
    Chat selectPrivateChatWithOtherUser(@Param("chatId") Long chatId, @Param("userId") Long userId);

    /**
     * 检查聊天是否存在
     */
    boolean chatExists(@Param("chatId") Long chatId);

    /**
     * 获取聊天类型
     */
    String selectChatType(@Param("chatId") Long chatId);

    /**
     * 获取聊天成员数量
     */
    Integer selectMemberCount(@Param("chatId") Long chatId);

    /**
     * 更新聊天信息
     */
    int updateChatInfo(@Param("chatId") Long chatId, @Param("name") String name, 
                       @Param("avatar") String avatar, @Param("notice") String notice);
}
