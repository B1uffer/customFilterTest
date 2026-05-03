package com.b1uffer.customfiltertest.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@RestController
public class ApiController {

    @GetMapping(value = "/api/data", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> data() {
        return Map.of("msg", "hello");
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> download() {
        byte[] payload = loadUserFile(); // 잠재적으로 .html
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=content.bin")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(payload);
    }

    private byte[] loadUserFile() {
        try {
            Path path = Path.of("uploads/user-file.bin"); // 저장된 파일의 경로 지정하기
            return Files.readAllBytes(path);
        } catch(IOException e) {
            throw new RuntimeException("파일 로드 실패",e);
        }
    }
}
