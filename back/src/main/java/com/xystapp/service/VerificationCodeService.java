package com.xystapp.service;

/**
 * 验证码服务接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
public interface VerificationCodeService {

    /**
     * 发送验证码
     */
    void sendVerificationCode(String username, String type);

    /**
     * 验证验证码
     */
    boolean verifyCode(String username, String code);

    /**
     * 生成验证码
     */
    String generateCode();

    /**
     * 存储验证码到Redis
     */
    void storeCode(String username, String code);

    /**
     * 删除验证码
     */
    void deleteCode(String username);
}
