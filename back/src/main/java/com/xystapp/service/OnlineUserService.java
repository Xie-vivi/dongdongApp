package com.xystapp.service;

import java.util.List;
import java.util.Set;

/**
 * 在线用户服务接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
public interface OnlineUserService {

    /**
     * 用户上线
     */
    void userOnline(Long userId, String sessionId);

    /**
     * 用户下线
     */
    void userOffline(Long userId, String sessionId);

    /**
     * 获取在线用户列表
     */
    Set<Long> getOnlineUsers();

    /**
     * 检查用户是否在线
     */
    boolean isUserOnline(Long userId);

    /**
     * 获取用户的会话ID列表
     */
    List<String> getUserSessions(Long userId);

    /**
     * 更新用户最后活跃时间
     */
    void updateUserLastActive(Long userId);

    /**
     * 获取用户最后活跃时间
     */
    Long getUserLastActive(Long userId);

    /**
     * 清理过期会话
     */
    void cleanExpiredSessions();
}
