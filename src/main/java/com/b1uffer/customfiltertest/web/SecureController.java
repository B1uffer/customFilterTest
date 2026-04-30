package com.b1uffer.customfiltertest.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecureController {
    @GetMapping("/api/secure/hello")
    public String secureHello() {
        return "This is secure endpoint!";
    }

    @GetMapping("/api/slow")
    public String slow() throws InterruptedException {
        Thread.sleep(1500); // 성능 필터 경고 유발 로직
        return "slow response done";
    }
}
