package com.marvin.campustrade.service;

import com.marvin.campustrade.data.dto.ProductDTO;
import com.marvin.campustrade.data.entity.Product;

public interface ProductService {
    ProductDTO.Response createProduct(ProductDTO.CreateRequest request);
}
