package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.service.TestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Service
public class TestServiceImpl implements TestService {

    private final String bucketName;
    private final S3Presigner presigner;

    public TestServiceImpl(@Value("${aws.s3.bucket:default-bucket}") String bucketName,
                           S3Presigner presigner) {
        this.bucketName = bucketName;
        this.presigner = S3Presigner.create();
    }

}
