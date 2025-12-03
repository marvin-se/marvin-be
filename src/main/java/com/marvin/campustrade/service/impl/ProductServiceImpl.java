package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.data.dto.ProductDTO;
import com.marvin.campustrade.data.entity.Image;
import com.marvin.campustrade.data.entity.Product;
import com.marvin.campustrade.data.entity.Users;
import com.marvin.campustrade.data.mapper.ProductMapper;
import com.marvin.campustrade.repository.ImageRepository;
import com.marvin.campustrade.repository.ProductRepository;
import com.marvin.campustrade.repository.UserRepository;
import com.marvin.campustrade.service.ProductService;
import com.marvin.campustrade.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    @Override
    public ProductDTO.Response createProduct(ProductDTO.CreateRequest request) {
        //Users user = userService.getCurrentUser();
        Users testUser = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Test user missing"));

        Product product = productMapper.toEntity(request);
        product.setUser(testUser);

        Product saved = productRepository.save(product);    // added new ad to db

        // save images manually
        if(request.getImages() != null) {
           for(String url : request.getImages()) {
               Image img = new Image();
               img.setImageUrl(url);
               img.setProduct(saved);
               imageRepository.save(img);
           }
        }

        List<Image> imgs = imageRepository.findByProduct(saved);
        List<String> urls = imgs.stream()
                .map(Image::getImageUrl)
                .toList();

        ProductDTO.Response response = productMapper.toResponse(saved);
        response.setImages(urls);

        return response;
    }
}
