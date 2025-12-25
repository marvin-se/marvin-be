package com.marvin.campustrade.service;

import com.marvin.campustrade.data.dto.ImageDTO;
import com.marvin.campustrade.data.entity.Product;

public interface ImageService {
    ImageDTO.PresignResponse presignUploads(
            Long productId,
            ImageDTO.PresignRequest request
    );
    // when a product is deleted,
    // all images of it need to be deleted
    void deleteImagesByProduct(Product product);
    void deleteImage(Long productId, String imageKey);
    ImageDTO.ImageListResponse getImagesWithPresignedUrls(Long productId);
}
