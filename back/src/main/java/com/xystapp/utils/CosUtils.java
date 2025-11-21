package com.xystapp.utils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 腾讯云COS文件上传工具类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Component
public class CosUtils {

    private static final Logger log = LoggerFactory.getLogger(CosUtils.class);

    @Value("${tencent.cos.secret-id}")
    private String secretId;

    @Value("${tencent.cos.secret-key}")
    private String secretKey;

    @Value("${tencent.cos.region}")
    private String region;

    @Value("${tencent.cos.bucket}")
    private String bucket;

    @Value("${tencent.cos.domain}")
    private String domain;

    @Value("${tencent.cos.base-path}")
    private String basePath;

    /**
     * 获取COS客户端
     */
    private COSClient getCosClient() {
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        return new COSClient(cred, clientConfig);
    }

    /**
     * 上传文件
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? 
            originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String fileName = UUID.randomUUID().toString() + extension;
        
        // 构建文件路径
        String key = basePath + folder + "/" + fileName;

        COSClient cosClient = null;
        try {
            cosClient = getCosClient();
            
            // 设置文件元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            
            // 上传文件
            InputStream inputStream = file.getInputStream();
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, inputStream, metadata);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            
            // 返回文件访问URL
            String fileUrl = domain + "/" + key;
            log.info("文件上传成功: {}", fileUrl);
            
            return fileUrl;
            
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage());
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        } finally {
            if (cosClient != null) {
                cosClient.shutdown();
            }
        }
    }

    /**
     * 上传头像
     */
    public String uploadAvatar(MultipartFile file) throws IOException {
        // 验证文件类型
        validateImageFile(file);
        return uploadFile(file, "avatars");
    }

    /**
     * 上传背景图
     */
    public String uploadBackground(MultipartFile file) throws IOException {
        // 验证文件类型
        validateImageFile(file);
        return uploadFile(file, "backgrounds");
    }

    /**
     * 上传帖子图片
     */
    public String uploadPostImage(MultipartFile file) throws IOException {
        // 验证文件类型
        validateImageFile(file);
        return uploadFile(file, "posts");
    }

    /**
     * 上传活动图片
     */
    public String uploadActivityImage(MultipartFile file) throws IOException {
        // 验证文件类型
        validateImageFile(file);
        return uploadFile(file, "activities");
    }

    /**
     * 验证图片文件
     */
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 检查文件大小 (5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("文件大小不能超过5MB");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("只能上传图片文件");
        }

        // 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        String extension = originalFilename.toLowerCase();
        if (!extension.endsWith(".jpg") && 
            !extension.endsWith(".jpeg") && 
            !extension.endsWith(".png") && 
            !extension.endsWith(".gif") && 
            !extension.endsWith(".webp")) {
            throw new IllegalArgumentException("只支持 JPG、PNG、GIF、WebP 格式的图片");
        }
    }

    /**
     * 删除文件
     */
    public void deleteFile(String fileUrl) {
        try {
            if (fileUrl == null || !fileUrl.contains(domain)) {
                return;
            }

            // 提取文件key
            String key = fileUrl.replace(domain + "/", "");
            
            COSClient cosClient = null;
            try {
                cosClient = getCosClient();
                cosClient.deleteObject(bucket, key);
                log.info("文件删除成功: {}", key);
            } finally {
                if (cosClient != null) {
                    cosClient.shutdown();
                }
            }
        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage());
        }
    }
}
