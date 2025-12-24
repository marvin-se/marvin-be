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
import com.marvin.campustrade.repository.FavouriteRepository;
import com.marvin.campustrade.repository.ImageRepository;
import com.marvin.campustrade.repository.ProductRepository;
import com.marvin.campustrade.repository.UserRepository;
import com.marvin.campustrade.service.ImageService;
import com.marvin.campustrade.service.ProductService;
import com.marvin.campustrade.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final FavouriteRepository favouriteRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public ProductDTO.Response createProduct(ProductDTO.CreateRequest request) {
        Users user = userService.getCurrentUser();
        Product product = productMapper.toEntity(request);
        product.setUser(user);
        product.setStatus(Status.AVAILABLE);

        Product saved = productRepository.save(product);    // added new ad to db

        ProductDTO.Response response = productMapper.toResponse(saved);
        response.setImages(List.of());

        return response;
    }

    @Override
    public List<ProductDTO.Response> getAllProducts() {
        List<Product> products = productRepository.findAll();

        List<Image> allImages = imageRepository.findAllByProductIn(products);

        Map<Long, List<String>> imagesByProductId =
                allImages.stream()
                        .collect(Collectors.groupingBy(
                                img -> img.getProduct().getId(),
                                Collectors.mapping(Image::getImageUrl, Collectors.toList())
                        ));

        Set<Long> favouriteProductIdsTemp = Set.of();
        try {
            Users currentUser = userService.getCurrentUser();
            favouriteProductIdsTemp =
                    favouriteRepository.findFavouriteProductIds(
                            currentUser, products
                    );
        } catch (Exception ignored) {
            favouriteProductIdsTemp = Set.of();
        }

        final Set<Long> favouriteProductIds = favouriteProductIdsTemp;

        return products.stream()
                .map(product ->
                        buildResponse(
                                product,
                                imagesByProductId.getOrDefault(product.getId(), List.of()),
                                favouriteProductIds
                        )
                )
                .toList();
    }

    @Override
    public ProductDTO.Response getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        List<String> images = imageRepository.findByProduct(product)
                .stream()
                .map(Image::getImageUrl).toList();

        Set<Long> favouriteProductIds;
        try {
            Users user = userService.getCurrentUser();
            favouriteProductIds = favouriteRepository
                    .findFavouriteProductIds(user, List.of(product));

            if(!product.getUser().getId().equals(user.getId())) {
                product.setVisitCount(product.getVisitCount() + 1);
                productRepository.save(product);
            }
        } catch (Exception e) {
            favouriteProductIds = Set.of();
            product.setVisitCount(product.getVisitCount() + 1);
            productRepository.save(product);
        }

        return buildResponse(product, images, favouriteProductIds);
    }

    @Override
    public void deleteProduct(Long id, Long sellerId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if(!product.getUser().getId().equals(sellerId)) {
            throw new UnauthorizedActionException("You can only delete your own products");
        }

        imageService.deleteImagesByProduct(product);

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

        List<Product> products = productRepository.findAllByUserId(sellerId);

        Map<Long, List<String>> imagesByProductId =
                imageRepository.findAllByProductIn(products)
                        .stream()
                        .collect(Collectors.groupingBy(
                                img -> img.getProduct().getId(),
                                Collectors.mapping(Image::getImageUrl, Collectors.toList())
                        ));

        Set<Long> favouriteProductIdsTemp;
        try {
            Users user = userService.getCurrentUser();
            favouriteProductIdsTemp = favouriteRepository.findFavouriteProductIds(user, products);
        } catch (Exception e) {
            favouriteProductIdsTemp = Set.of();
        }
        final Set<Long> favouriteIds = favouriteProductIdsTemp;
        return products.stream()
                .map(product ->
                        buildResponse(
                                product,
                                imagesByProductId.getOrDefault(product.getId(), List.of()),
                                favouriteIds
                        )
                ).toList();
    }

    private void applyFavouriteFlag(Product product, ProductDTO.Response response) {
        try {
            Users currentUser = userService.getCurrentUser();

            boolean isFavourite = favouriteRepository
                    .existsByUserAndProduct(currentUser, product);

            response.setIsFavourite(isFavourite);

        } catch (Exception e) {
            // not authenticated
            response.setIsFavourite(false);
        }
    }

    @Override
    @Transactional
    public void saveImages(Long productId, List<String> keys) {
        if(keys == null || keys.isEmpty()) {
            throw new InvalidRequestFieldException("Keys cannot be empty");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        for(String key : keys) {
            if(key == null || key.isBlank()) { continue; }

            Image img = new Image();
            img.setProduct(product);
            img.setImageUrl(key);
            imageRepository.save(img);
        }
    }

    @Override
    @Transactional
    public void replaceImages(Long productId, List<String> keys) {
        if(keys == null || keys.isEmpty()) {
            throw new InvalidRequestFieldException("Keys cannot be empty");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        imageService.deleteImagesByProduct(product);

        for(String key : keys) {
            Image img = new Image();
            img.setProduct(product);
            img.setImageUrl(key);
            imageRepository.save(img);
        }
    }

    private ProductDTO.Response buildResponse(Product product, List<String> images, Set<Long> favouriteProductIds) {
        ProductDTO.Response response = productMapper.toResponse(product);

        response.setImages(images);

        response.setIsFavourite(
                favouriteProductIds != null &&
                favouriteProductIds.contains(product.getId())
        );

        return response;
    }
}
