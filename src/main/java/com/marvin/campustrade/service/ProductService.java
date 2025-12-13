package com.marvin.campustrade.service;

import com.marvin.campustrade.data.dto.ProductDTO;
import com.marvin.campustrade.data.entity.Product;

import java.util.List;

public interface ProductService {
    ProductDTO.Response createProduct(ProductDTO.CreateRequest request);
    List<ProductDTO.Response> getAllProducts();
    ProductDTO.Response getProductById(Long id);
}
