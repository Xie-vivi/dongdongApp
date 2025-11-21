package com.xystapp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * XYST社交平台后台应用启动类
 * 
 * @author XYST Team
 * @version 1.0.0
 * @since 2025-11-16
 */
@SpringBootApplication
@MapperScan("com.xystapp.mapper")
public class XystappApplication {

    public static void main(String[] args) {
        SpringApplication.run(XystappApplication.class, args);
        System.out.println("=================================");
        System.out.println("  XYST社交平台后台API启动成功！");
        System.out.println("  访问地址: http://localhost:8080/api");
        System.out.println("  API文档: http://localhost:8080/api/doc.html");
        System.out.println("=================================");
    }
}
