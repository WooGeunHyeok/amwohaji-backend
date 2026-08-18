package com.amwohaji.backend.global.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootRedirectController {

    @GetMapping({"/", "/index"})
    public String redirectToSwagger() {
        // 루트(/)로 접근하면 Swagger UI 페이지로 리다이렉트 시킵니다.
        return "redirect:/swagger-ui/index.html";
    }
}