package com.amwohaji.backend.global.file;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * application*.properties의 file.upload.* 값을 바인딩한다.
 *
 * file.upload.base-dir   : 파일이 물리적으로 저장되는 서버 디스크 경로
 * file.upload.base-url   : base-dir에 매핑된, 외부에서 접근 가능한 URL prefix (WebMvcConfig의 리소스 핸들러와 짝을 이룸)
 * file.upload.max-file-size : 업로드 허용 최대 용량 (byte). 0 이하면 제한 없음
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "file.upload")
public class FileStorageProperties {

    private String baseDir = System.getProperty("java.io.tmpdir") + "/amwohaji-upload";
    private String baseUrl = "/files";
    private long maxFileSize = 20L * 1024 * 1024;
}
