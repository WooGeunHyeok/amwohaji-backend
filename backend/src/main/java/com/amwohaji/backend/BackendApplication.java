package com.amwohaji.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

    static {
        // static 블록: main() 진입 여부와 무관하게 클래스 로딩 시점에 항상 실행됨
        // ./gradlew test 처럼 main()을 거치지 않는 테스트 컨텍스트에서도 .env가 적용되도록 함
        Dotenv.configure()
                .ignoreIfMissing()
                .systemProperties()
                .load();
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
