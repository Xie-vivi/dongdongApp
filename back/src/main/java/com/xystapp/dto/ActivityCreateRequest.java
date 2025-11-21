package com.xystapp.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 活动创建请求DTO
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Data
@ApiModel("活动创建请求")
public class ActivityCreateRequest {

    @ApiModelProperty(value = "活动标题", required = true)
    @NotBlank(message = "活动标题不能为空")
    @Size(max = 200, message = "活动标题长度不能超过200个字符")
    private String title;

    @ApiModelProperty(value = "活动描述")
    @Size(max = 2000, message = "活动描述长度不能超过2000个字符")
    private String description;

    @ApiModelProperty(value = "活动日期", required = true)
    @NotNull(message = "活动日期不能为空")
    @Future(message = "活动日期必须是未来时间")
    private LocalDate date;

    @ApiModelProperty(value = "活动星期")
    @Size(max = 10, message = "星期信息长度不能超过10个字符")
    private String day;

    @ApiModelProperty(value = "活动时间")
    @Size(max = 50, message = "活动时间长度不能超过50个字符")
    private String time;

    @ApiModelProperty(value = "活动地点")
    @Size(max = 100, message = "活动地点长度不能超过100个字符")
    private String location;

    @ApiModelProperty(value = "活动标签")
    @Size(max = 50, message = "标签长度不能超过50个字符")
    private String tag;

    @ApiModelProperty(value = "最大参与人数")
    @Min(value = 1, message = "最大参与人数必须大于0")
    @Max(value = 10000, message = "最大参与人数不能超过10000")
    private Integer maxParticipants;

    @ApiModelProperty(value = "活动图片列表")
    private List<String> images;

    @ApiModelProperty(value = "报名截止时间")
    private LocalDateTime signupDeadline;

    @ApiModelProperty(value = "活动封面图片")
    @Size(max = 255, message = "封面图片URL长度不能超过255个字符")
    private String image;

    @ApiModelProperty(value = "活动状态", allowableValues = "draft,published")
    private String status = "draft";

    /**
     * 自定义验证：报名截止时间必须早于活动开始时间
     */
    @AssertTrue(message = "报名截止时间必须早于活动开始时间")
    public boolean isSignupDeadlineValid() {
        if (signupDeadline == null || date == null) {
            return true; // 如果其中一个为空，跳过验证
        }
        
        // 将活动日期和时间组合成LocalDateTime进行比较
        LocalDateTime activityDateTime = null;
        if (time != null) {
            // 简单处理：假设时间格式为"HH:mm"或"HH:mm:ss"
            try {
                String[] timeParts = time.split(":");
                int hour = Integer.parseInt(timeParts[0]);
                int minute = timeParts.length > 1 ? Integer.parseInt(timeParts[1]) : 0;
                activityDateTime = date.atTime(hour, minute);
            } catch (Exception e) {
                // 如果时间格式解析失败，默认使用当天的23:59
                activityDateTime = date.atTime(23, 59);
            }
        } else {
            // 如果没有指定时间，默认使用当天的23:59
            activityDateTime = date.atTime(23, 59);
        }
        
        return signupDeadline.isBefore(activityDateTime);
    }

    // ==================== Getter和Setter方法 ====================

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public LocalDateTime getSignupDeadline() { return signupDeadline; }
    public void setSignupDeadline(LocalDateTime signupDeadline) { this.signupDeadline = signupDeadline; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
