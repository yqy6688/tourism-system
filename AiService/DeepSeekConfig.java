package org.example.springboot.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * DeepSeek API配置类
 * 用于加载和管理DeepSeek API的配置信息
 */
@Data
@Component
@ConfigurationProperties(prefix = "deepseek.api")
public class DeepSeekConfig {
    private String baseUrl;
    private String apiKey;
    private String model;
}
