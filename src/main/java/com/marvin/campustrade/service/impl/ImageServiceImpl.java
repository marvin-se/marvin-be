package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.data.dto.ImageDTO;
import com.marvin.campustrade.data.entity.Image;
import com.marvin.campustrade.data.entity.Product;
import com.marvin.campustrade.repository.ImageRepository;
import com.marvin.campustrade.service.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.*;

@Service
public class ImageServiceImpl implements ImageService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final ImageRepository imageRepository;
    private final String bucketName;
    private final S3Presigner presigner;
    private final S3Client s3Client;

    public ImageServiceImpl(
            ImageRepository imageRepository,
            @Value("${aws.s3.bucket}") String bucketName,
            S3Presigner presigner,
            S3Client s3Client
    ) {
        this.imageRepository = imageRepository;
        this.bucketName = bucketName;
        this.presigner = presigner;
        this.s3Client = s3Client;
    }

    @Override
    public ImageDTO.PresignResponse presignUploads(
            Long productId,
            ImageDTO.PresignRequest request
    ) {

        List<ImageDTO.PresignedImage> result = new ArrayList<>();

        for (ImageDTO.ImageItem item : request.getImages()) {

            if (!ALLOWED_TYPES.contains(item.getContentType())) {
                throw new IllegalArgumentException(
                        "Unsupported content type: " + item.getContentType()
                );
            }

            String extension = extensionFromContentType(item.getContentType());

            String key = "products/"
                    + productId
                    + "/"
                    + UUID.randomUUID()
                    + extension;

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(item.getContentType())
                    .build();

            PutObjectPresignRequest presignRequest =
                    PutObjectPresignRequest.builder()
                            .signatureDuration(Duration.ofMinutes(5))
                            .putObjectRequest(putRequest)
                            .build();

            PresignedPutObjectRequest presigned =
                    presigner.presignPutObject(presignRequest);

            result.add(new ImageDTO.PresignedImage(
                    key,
                    presigned.url().toString()
            ));
        }

        return new ImageDTO.PresignResponse(result);
    }

    private String extensionFromContentType(String type) {
        return switch (type) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> "";
        };
    }

    @Override
    public void deleteImagesByProduct(Product product) {

        // fetch keys
        List<Image> images = imageRepository.findByProduct(product);

        // delete from S3
        for (Image img : images) {
            String key = img.getImageUrl();
            if (key == null || key.isBlank()) continue;

            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build()
            );
        }

        // delete DB rows
        imageRepository.deleteAllByProduct(product);
    }
}
