package com.marvin.campustrade.service;

import com.marvin.campustrade.data.dto.ProductDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductService {
    ProductDTO.Response createProduct(ProductDTO.CreateRequest request);
    void publishProduct(Long productId);
    List<ProductDTO.Response> getAllProducts();
    ProductDTO.Response getProductById(Long id);
    void deleteProduct(Long id, Long sellerId);
    ProductDTO.Response updateProduct(Long id, ProductDTO.UpdateRequest request,Long sellerId);
    ProductDTO.Response markAsSold(Long id, Long sellerId);
    List<ProductDTO.Response> getSellerProducts(Long sellerId, Long currentUserId);
    void saveImages(Long productId, List<String> keys);
    void replaceImages(Long productId, List<String> keys);
}
