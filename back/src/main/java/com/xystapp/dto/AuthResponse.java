package com.xystapp.dto;

import lombok.Data;

/**
 * 认证响应DTO
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Data
public class AuthResponse {

    private String token;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserInfo userInfo;

    public AuthResponse(String token, String refreshToken, Long expiresIn, UserInfo userInfo) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.userInfo = userInfo;
    }

    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String nickname;
        private String avatar;
        private String uid;
        private Boolean isCertified;

        public UserInfo(Long id, String username, String nickname, String avatar, String uid, Boolean isCertified) {
            this.id = id;
            this.username = username;
            this.nickname = nickname;
            this.avatar = avatar;
            this.uid = uid;
            this.isCertified = isCertified;
        }
    }
}
