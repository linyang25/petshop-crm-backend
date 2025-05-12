package com.petshop.crmbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalCorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        .addMapping("/**")                    // 对所有接口都开启
                        .allowedOriginPatterns("*")                  // 允许任意来源，建议上线改成前端地址
                        .allowedMethods("GET","POST","PUT","DELETE","OPTIONS") // 放行常见请求方法
                        .allowedHeaders("*")                  // 允许任意请求头
                        .allowCredentials(true)               // 如果要传 cookie／认证信息就设 true
                        .maxAge(3600);                        // 预检请求的缓存时间（秒）
            }
        };
    }
}