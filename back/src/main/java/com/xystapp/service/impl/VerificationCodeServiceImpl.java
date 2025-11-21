package com.xystapp.service.impl;

import com.xystapp.entity.User;
import com.xystapp.exception.BusinessException;
import com.xystapp.mapper.UserMapper;
import com.xystapp.service.VerificationCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    private static final Logger log = LoggerFactory.getLogger(VerificationCodeServiceImpl.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JavaMailSender mailSender;

    private static final String CODE_PREFIX = "verification_code:";
    private static final String RATE_LIMIT_PREFIX = "rate_limit:";
    private static final String ATTEMPT_PREFIX = "attempt_count:";
    private static final int CODE_EXPIRE_MINUTES = 10;
    private static final int CODE_LENGTH = 6;
    private static final int MAX_ATTEMPTS = 5;
    private static final int RATE_LIMIT_MINUTES = 15;
    private static final int MAX_REQUESTS_PER_PERIOD = 3;

    @Override
    public void sendVerificationCode(String username, String type) {
        // 验证用户是否存在
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 检查频率限制
        checkRateLimit(username);

        // 生成验证码
        String code = generateCode();

        // 存储验证码到Redis
        storeCode(username, code);

        // 记录发送次数
        incrementRateLimit(username);

        try {
            if ("email".equals(type)) {
                sendEmailCode(user, code);
            } else if ("phone".equals(type)) {
                sendPhoneCode(user, code);
            } else {
                throw new BusinessException(400, "不支持的验证方式");
            }

            log.info("验证码发送成功: {}, 类型: {}", username, type);
        } catch (Exception e) {
            log.error("验证码发送失败: {}, 错误: {}", username, e.getMessage());
            throw new BusinessException(500, "验证码发送失败，请稍后重试");
        }
    }

    @Override
    public boolean verifyCode(String username, String code) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(code)) {
            return false;
        }

        // 检查尝试次数限制
        checkAttemptLimit(username);

        String key = CODE_PREFIX + username;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            return false;
        }

        boolean isValid = storedCode.equals(code);
        
        if (isValid) {
            // 验证成功后删除验证码和重置尝试次数
            deleteCode(username);
            resetAttempts(username);
        } else {
            // 验证失败，增加尝试次数
            incrementAttempts(username);
        }

        return isValid;
    }

    @Override
    public String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    @Override
    public void storeCode(String username, String code) {
        String key = CODE_PREFIX + username;
        redisTemplate.opsForValue().set(key, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
    }

    @Override
    public void deleteCode(String username) {
        String key = CODE_PREFIX + username;
        redisTemplate.delete(key);
    }

    /**
     * 发送邮件验证码
     */
    private void sendEmailCode(User user, String code) {
        // 暂时使用模拟发送（实际项目中需要配置邮件服务器）
        log.info("模拟发送邮件验证码 - 用户: {}, 昵称: {}, 验证码: {}", 
                user.getUsername(), user.getNickname(), code);
        
        // 实际项目中，这里应该调用邮件服务发送验证码
        // 由于当前数据库没有email字段，暂时使用控制台日志
    }

    /**
     * 发送短信验证码
     */
    private void sendPhoneCode(User user, String code) {
        // 暂时使用模拟发送（实际项目中需要集成短信服务商）
        log.info("模拟发送短信验证码 - 用户: {}, 昵称: {}, 验证码: {}", 
                user.getUsername(), user.getNickname(), code);
        
        // 实际项目中，这里应该调用短信服务发送验证码
        // 由于当前数据库没有phone字段，暂时使用控制台日志
    }

    /**
     * 检查频率限制
     */
    private void checkRateLimit(String username) {
        String key = RATE_LIMIT_PREFIX + username;
        String count = redisTemplate.opsForValue().get(key);
        
        if (count != null && Integer.parseInt(count) >= MAX_REQUESTS_PER_PERIOD) {
            throw new BusinessException(429, "发送过于频繁，请" + RATE_LIMIT_MINUTES + "分钟后再试");
        }
    }

    /**
     * 增加频率限制计数
     */
    private void incrementRateLimit(String username) {
        String key = RATE_LIMIT_PREFIX + username;
        Long count = redisTemplate.opsForValue().increment(key);
        
        if (count == 1) {
            // 第一次设置过期时间
            redisTemplate.expire(key, RATE_LIMIT_MINUTES, TimeUnit.MINUTES);
        }
    }

    /**
     * 检查尝试次数限制
     */
    private void checkAttemptLimit(String username) {
        String key = ATTEMPT_PREFIX + username;
        String count = redisTemplate.opsForValue().get(key);
        
        if (count != null && Integer.parseInt(count) >= MAX_ATTEMPTS) {
            throw new BusinessException(429, "验证失败次数过多，请稍后重试");
        }
    }

    /**
     * 增加尝试次数
     */
    private void incrementAttempts(String username) {
        String key = ATTEMPT_PREFIX + username;
        Long count = redisTemplate.opsForValue().increment(key);
        
        if (count == 1) {
            // 第一次设置过期时间（1小时）
            redisTemplate.expire(key, 1, TimeUnit.HOURS);
        }
    }

    /**
     * 重置尝试次数
     */
    private void resetAttempts(String username) {
        String key = ATTEMPT_PREFIX + username;
        redisTemplate.delete(key);
    }
}
