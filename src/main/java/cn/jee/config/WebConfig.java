package cn.jee.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${default.images.path}")
    private String defaultImagesPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将外部图片目录映射为 /images/** 的 URL 路径
        // 使得存储在 D:/tools/images/ 下的图片可通过 http://localhost:8080/images/<文件名> 访问
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + defaultImagesPath);
    }
}
