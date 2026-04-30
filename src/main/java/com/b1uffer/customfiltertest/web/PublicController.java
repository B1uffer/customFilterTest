package com.b1uffer.customfiltertest.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicController {
    @GetMapping("/public/hello")
    public String hello(
            @RequestParam(defaultValue = "world") String name,
            @RequestParam(required = false) String password
    ) {
        // password 파라미터는 로깅 필터에서 마스킹이 되는지 필터링용
        return "Hello" + name + "!";
    }
}
