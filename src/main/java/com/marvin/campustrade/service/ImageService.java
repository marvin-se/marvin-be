package com.marvin.campustrade.service;

import com.marvin.campustrade.data.dto.ImageDTO;
import com.marvin.campustrade.data.entity.Product;

import java.util.List;

public interface ImageService {
    ImageDTO.PresignResponse presignUploads(
            Long productId,
            ImageDTO.PresignRequest request
    );

    void deleteImagesByProduct(Product product);
}
