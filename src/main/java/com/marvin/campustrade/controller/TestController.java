package com.marvin.campustrade.controller;

import com.marvin.campustrade.service.impl.TestServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final TestServiceImpl service;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("image") MultipartFile image,@RequestParam("userId") Long userId) throws IOException, IOException {
        return ResponseEntity.ok("File uploaded successfully! URL: " + service.uploadFile(image, userId));
    }

    //ben burada image-url kullandım da siz tabi image id zart zurt fln db'yle ilişkilendirerek kullanırsınız.
    //ben sadece s3 delete fonksiyonunu göstermek için böyle yaptım
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam("image-url") String imageUrl) {
        service.deleteFile(imageUrl);
        return ResponseEntity.ok("File deletion endpoint hit for URL: " + imageUrl);
    }
}
