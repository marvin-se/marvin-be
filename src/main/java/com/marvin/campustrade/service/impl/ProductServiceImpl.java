package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.constants.Status;
import com.marvin.campustrade.data.dto.ProductDTO;
import com.marvin.campustrade.data.entity.Image;
import com.marvin.campustrade.data.entity.Product;
import com.marvin.campustrade.data.entity.Users;
import com.marvin.campustrade.data.mapper.ProductMapper;
import com.marvin.campustrade.exception.InvalidRequestFieldException;
import com.marvin.campustrade.exception.ProductNotFoundException;
import com.marvin.campustrade.exception.UnauthorizedActionException;
import com.marvin.campustrade.exception.UserNotFoundException;
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
    private final ImageRepository imageRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public ProductDTO.Response createProduct(ProductDTO.CreateRequest request) {
        Users user = userService.getCurrentUser();
        Product product = productMapper.toEntity(request);
        product.setUser(user);
        product.setStatus(Status.AVAILABLE);

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

    @Override
    public List<ProductDTO.Response> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    public ProductDTO.Response getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        ProductDTO.Response response = productMapper.toResponse(product);

        // check owner safely
        try {
            Users currentUser = userService.getCurrentUser();

            if (product.getUser().getId().equals(currentUser.getId())) {
                productMapper.includeOwnerMetrics(product, response);
            }
        } catch (Exception ignored) {
            // user not authenticated → public view only
        }
        return response;
    }

    @Override
    public void deleteProduct(Long id, Long sellerId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if(!product.getUser().getId().equals(sellerId)) {
            throw new UnauthorizedActionException("You can only delete your own products");
        }

        productRepository.delete(product);
    }

    @Override
    public ProductDTO.Response updateProduct(Long id, ProductDTO.UpdateRequest request,Long sellerId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if(!product.getUser().getId().equals(sellerId)) {
            throw new UnauthorizedActionException("You can only update your own products");
        }

        // treat empty strings as null (do not overwrite)
        if (request.getDescription() != null && request.getDescription().isBlank()) {
            request.setDescription(null);
        }
        if (request.getTitle() != null && request.getTitle().isBlank()) {
            request.setTitle(null);
        }

        productMapper.updateEntityFromDto(request, product);

        Product updated = productRepository.save(product);
        return productMapper.toResponse(updated);
    }

    @Override
    public ProductDTO.Response markAsSold(Long id, Long sellerId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        if(!product.getUser().getId().equals(sellerId)) {
            throw new UnauthorizedActionException("You can only mark your own products");
        }

        if(product.getStatus() == Status.SOLD) {
            throw new InvalidRequestFieldException("Ad is already marked as sold.");
        }

        product.setStatus(Status.SOLD);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public List<ProductDTO.Response> getSellerProducts(Long sellerId, Long currentUserId) {
        // boolean check
        if (!userRepository.existsById(sellerId)) {
            throw new UserNotFoundException("User not found");
        }
        boolean isOwner = sellerId.equals(currentUserId);

        return productRepository.findAllByUserId(sellerId)
                .stream()
                .map(product -> {
                    ProductDTO.Response response =
                            productMapper.toResponse(product);

                    if (isOwner) {
                        productMapper.includeOwnerMetrics(product, response);
                    }
                    return response;
                })
                .toList();
    }

}
