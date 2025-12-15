package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.service.TestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Service
public class TestServiceImpl implements TestService {

    private final S3Client s3Client;
    private final String bucketName;

    public TestServiceImpl(S3Client s3Client,
                           @Value("${aws.s3.bucket:default-bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public String uploadFile(MultipartFile image, Long userId) throws IOException {

        //bucketa kaydetmek için origanl filename'i uniqleştiriyoruz
        String key = userId + "/" +
                UUID.randomUUID() + "-" + image.getOriginalFilename();

        //s3 client requesti
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(image.getContentType())
                .build();

        //requesti s3 e gönderiyoruz
        s3Client.putObject(
                request,
                RequestBody.fromInputStream(image.getInputStream(), image.getSize())
        );

        //image in s3 teki url ini döndürüyoruz
        return s3Client.utilities()
                .getUrl(b -> b.bucket(bucketName).key(key))
                .toExternalForm();
    }

    public void deleteFile(String imageUrl) {

        // this is the url: https://marvin-test-v1.s3.eu-north-1.amazonaws.com/uploads/image.png
        // we need the key: uploads/image.png

        // private function to extract key from url
        String imageKey = extractKeyFromUrl(imageUrl);

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(imageKey)   // e.g. "uploads/image.png"
                .build();

        s3Client.deleteObject(request);
    }

    private String extractKeyFromUrl(String url) {
        return URI.create(url).getPath().substring(1);
    }

}
