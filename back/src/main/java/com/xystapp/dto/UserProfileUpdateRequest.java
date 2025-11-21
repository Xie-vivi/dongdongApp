package com.xystapp.dto;

import lombok.Data;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 用户资料更新请求DTO
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Data
public class UserProfileUpdateRequest {

    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;

    @Pattern(regexp = "^(male|female|other)$", message = "性别只能是male、female或other")
    private String gender;

    @Size(max = 500, message = "个人简介长度不能超过500个字符")
    private String bio;

    @Size(max = 100, message = "学校名称长度不能超过100个字符")
    private String school;

    @Size(max = 50, message = "位置信息长度不能超过50个字符")
    private String location;

    @Pattern(regexp = "^[A-Z]{4}$", message = "MBTI类型必须是4位大写字母")
    private String mbti;

    private Boolean showGender;

    private Boolean showAge;

    private Boolean showMbti;

    // ==================== Getter和Setter方法 ====================

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMbti() {
        return mbti;
    }

    public void setMbti(String mbti) {
        this.mbti = mbti;
    }

    public Boolean getShowGender() {
        return showGender;
    }

    public void setShowGender(Boolean showGender) {
        this.showGender = showGender;
    }

    public Boolean getShowAge() {
        return showAge;
    }

    public void setShowAge(Boolean showAge) {
        this.showAge = showAge;
    }

    public Boolean getShowMbti() {
        return showMbti;
    }

    public void setShowMbti(Boolean showMbti) {
        this.showMbti = showMbti;
    }
}
