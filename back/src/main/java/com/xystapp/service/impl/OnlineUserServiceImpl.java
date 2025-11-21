package com.xystapp.service.impl;

import com.xystapp.service.OnlineUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 在线用户服务实现
 * 使用Redis存储用户在线状态和会话信息
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Service
public class OnlineUserServiceImpl implements OnlineUserService {

    private static final Logger log = LoggerFactory.getLogger(OnlineUserServiceImpl.class);

    private static final String ONLINE_USERS_KEY = "online:users";
    private static final String USER_SESSIONS_KEY = "online:sessions:user:";
    private static final String SESSION_USER_KEY = "online:session:user:";
    private static final String USER_LAST_ACTIVE_KEY = "online:last_active:user:";
    private static final long SESSION_TIMEOUT = 30; // 30分钟超时
    private static final long LAST_ACTIVE_TIMEOUT = 60; // 60分钟最后活跃时间

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void userOnline(Long userId, String sessionId) {
        try {
            // 1. 将用户添加到在线用户集合
            redisTemplate.opsForSet().add(ONLINE_USERS_KEY, userId);
            redisTemplate.expire(ONLINE_USERS_KEY, LAST_ACTIVE_TIMEOUT, TimeUnit.MINUTES);

            // 2. 将会话ID添加到用户的会话列表
            String userSessionsKey = USER_SESSIONS_KEY + userId;
            redisTemplate.opsForSet().add(userSessionsKey, sessionId);
            redisTemplate.expire(userSessionsKey, SESSION_TIMEOUT, TimeUnit.MINUTES);

            // 3. 建立会话到用户的映射
            String sessionUserKey = SESSION_USER_KEY + sessionId;
            redisTemplate.opsForValue().set(sessionUserKey, userId, SESSION_TIMEOUT, TimeUnit.MINUTES);

            // 4. 更新用户最后活跃时间
            updateUserLastActive(userId);

            log.info("用户上线: userId={}, sessionId={}", userId, sessionId);

        } catch (Exception e) {
            log.error("记录用户上线状态失败: userId={}, sessionId={}, error={}", 
                    userId, sessionId, e.getMessage(), e);
        }
    }

    @Override
    public void userOffline(Long userId, String sessionId) {
        try {
            // 1. 从用户的会话列表中移除会话ID
            String userSessionsKey = USER_SESSIONS_KEY + userId;
            redisTemplate.opsForSet().remove(userSessionsKey, sessionId);

            // 2. 删除会话到用户的映射
            String sessionUserKey = SESSION_USER_KEY + sessionId;
            redisTemplate.delete(sessionUserKey);

            // 3. 检查用户是否还有其他会话
            Set<Object> remainingSessions = redisTemplate.opsForSet().members(userSessionsKey);
            if (remainingSessions == null || remainingSessions.isEmpty()) {
                // 如果没有其他会话，将用户从在线用户集合中移除
                redisTemplate.opsForSet().remove(ONLINE_USERS_KEY, userId);
                redisTemplate.delete(userSessionsKey);
                
                log.info("用户完全下线: userId={}, sessionId={}", userId, sessionId);
            } else {
                log.info("用户会话下线: userId={}, sessionId={}, remainingSessions={}", 
                        userId, sessionId, remainingSessions.size());
            }

        } catch (Exception e) {
            log.error("记录用户下线状态失败: userId={}, sessionId={}, error={}", 
                    userId, sessionId, e.getMessage(), e);
        }
    }

    @Override
    public Set<Long> getOnlineUsers() {
        try {
            Set<Object> onlineUsers = redisTemplate.opsForSet().members(ONLINE_USERS_KEY);
            if (onlineUsers == null) {
                return new HashSet<>();
            }
            
            return onlineUsers.stream()
                    .map(obj -> Long.valueOf(obj.toString()))
                    .collect(Collectors.toSet());
                    
        } catch (Exception e) {
            log.error("获取在线用户列表失败: error={}", e.getMessage(), e);
            return new HashSet<>();
        }
    }

    @Override
    public boolean isUserOnline(Long userId) {
        try {
            return redisTemplate.opsForSet().isMember(ONLINE_USERS_KEY, userId);
        } catch (Exception e) {
            log.error("检查用户在线状态失败: userId={}, error={}", userId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<String> getUserSessions(Long userId) {
        try {
            String userSessionsKey = USER_SESSIONS_KEY + userId;
            Set<Object> sessions = redisTemplate.opsForSet().members(userSessionsKey);
            
            if (sessions == null) {
                return new ArrayList<>();
            }
            
            return sessions.stream()
                    .map(obj -> obj.toString())
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("获取用户会话列表失败: userId={}, error={}", userId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public void updateUserLastActive(Long userId) {
        try {
            String userLastActiveKey = USER_LAST_ACTIVE_KEY + userId;
            long currentTime = System.currentTimeMillis();
            
            redisTemplate.opsForValue().set(userLastActiveKey, currentTime, 
                    LAST_ACTIVE_TIMEOUT, TimeUnit.MINUTES);
                    
        } catch (Exception e) {
            log.error("更新用户最后活跃时间失败: userId={}, error={}", userId, e.getMessage(), e);
        }
    }

    @Override
    public Long getUserLastActive(Long userId) {
        try {
            String userLastActiveKey = USER_LAST_ACTIVE_KEY + userId;
            Object lastActive = redisTemplate.opsForValue().get(userLastActiveKey);
            
            if (lastActive != null) {
                return Long.valueOf(lastActive.toString());
            }
            
            return null;
            
        } catch (Exception e) {
            log.error("获取用户最后活跃时间失败: userId={}, error={}", userId, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void cleanExpiredSessions() {
        try {
            // 清理过期的在线用户
            Set<Object> onlineUsers = redisTemplate.opsForSet().members(ONLINE_USERS_KEY);
            if (onlineUsers != null) {
                for (Object userObj : onlineUsers) {
                    Long userId = Long.valueOf(userObj.toString());
                    
                    // 检查用户最后活跃时间
                    Long lastActive = getUserLastActive(userId);
                    if (lastActive == null || 
                        (System.currentTimeMillis() - lastActive) > LAST_ACTIVE_TIMEOUT * 60 * 1000) {
                        
                        // 用户已过期，清理相关数据
                        String userSessionsKey = USER_SESSIONS_KEY + userId;
                        Set<Object> sessions = redisTemplate.opsForSet().members(userSessionsKey);
                        
                        if (sessions != null) {
                            for (Object sessionObj : sessions) {
                                String sessionId = sessionObj.toString();
                                String sessionUserKey = SESSION_USER_KEY + sessionId;
                                redisTemplate.delete(sessionUserKey);
                            }
                        }
                        
                        redisTemplate.delete(userSessionsKey);
                        redisTemplate.opsForSet().remove(ONLINE_USERS_KEY, userId);
                        
                        log.info("清理过期用户: userId={}", userId);
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("清理过期会话失败: error={}", e.getMessage(), e);
        }
    }

    /**
     * 根据会话ID获取用户ID
     */
    public Long getUserIdBySession(String sessionId) {
        try {
            String sessionUserKey = SESSION_USER_KEY + sessionId;
            Object userId = redisTemplate.opsForValue().get(sessionUserKey);
            
            if (userId != null) {
                return Long.valueOf(userId.toString());
            }
            
            return null;
            
        } catch (Exception e) {
            log.error("根据会话ID获取用户ID失败: sessionId={}, error={}", sessionId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取在线用户数量
     */
    public long getOnlineUserCount() {
        try {
            return redisTemplate.opsForSet().size(ONLINE_USERS_KEY);
        } catch (Exception e) {
            log.error("获取在线用户数量失败: error={}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 批量检查用户在线状态
     */
    public Map<Long, Boolean> batchCheckUserOnline(Set<Long> userIds) {
        Map<Long, Boolean> result = new HashMap<>();
        
        try {
            for (Long userId : userIds) {
                result.put(userId, isUserOnline(userId));
            }
        } catch (Exception e) {
            log.error("批量检查用户在线状态失败: error={}", e.getMessage(), e);
            // 返回所有用户为离线状态
            userIds.forEach(userId -> result.put(userId, false));
        }
        
        return result;
    }
}
