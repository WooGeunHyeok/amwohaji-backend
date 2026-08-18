package com.amwohaji.backend.global.config;

import com.amwohaji.backend.global.file.FileStorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

/**
 * file.upload.base-dir 하위 물리 파일을 "/files/**" URL로 그대로 서빙한다.
 * FileStorageService가 만드는 FILE_URL(= file.upload.base-url + 상대경로)과 짝을 이룬다.
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final FileStorageProperties fileStorageProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Path.of(fileStorageProperties.getBaseDir()).toUri().toString();

        registry.addResourceHandler("/files/**")
                .addResourceLocations(location);
    }
}
