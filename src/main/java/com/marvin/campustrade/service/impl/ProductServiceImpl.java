package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.constants.Status;
import com.marvin.campustrade.data.dto.ProductDTO;
import com.marvin.campustrade.data.entity.*;
import com.marvin.campustrade.data.mapper.ProductMapper;
import com.marvin.campustrade.exception.*;
import com.marvin.campustrade.repository.*;
import com.marvin.campustrade.service.ImageService;
import com.marvin.campustrade.service.ProductService;
import com.marvin.campustrade.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
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
    private final ConversationRepository conversationRepository;
    private final TransactionRepository transactionRepository;
    private final UsersBlockRepository usersBlockRepository;

    @Override
    public ProductDTO.Response createProduct(ProductDTO.CreateRequest request) {
        Users user = userService.getCurrentUser();
        Product product = productMapper.toEntity(request);
        product.setUser(user);
        product.setStatus(Status.DRAFT);

        Product saved = productRepository.save(product);    // added new ad to db

        ProductDTO.Response response = productMapper.toResponse(saved);
        response.setImages(List.of());

        return response;
    }

    @Transactional
    @Override
    public void publishProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        long imageCount = imageRepository.countByProduct(product);
        if(imageCount < 1) {
            throw new IllegalStateException("Product must have at least one image");
        }
        product.setStatus(Status.AVAILABLE);
    }

    @Override
    public List<ProductDTO.Response> getAllProducts() {

        Users currentUser = null;
        Set<Long> blockedUserIds = Set.of();

        try {
            currentUser = userService.getCurrentUser();

            Set<Long> blockedByMe =
                    usersBlockRepository.findBlockedUserIds(currentUser);

            Set<Long> blockedMe =
                    usersBlockRepository.findUsersWhoBlockedMeIds(currentUser);

            blockedUserIds = new HashSet<>();
            blockedUserIds.addAll(blockedByMe);
            blockedUserIds.addAll(blockedMe);

        } catch (Exception ignored) {
            // User not logged in → show all products
        }

        List<Product> products;
        if (blockedUserIds.isEmpty()) {
            products = productRepository.findAllByStatus(Status.AVAILABLE);
        } else {
            products = productRepository.findAvailableProductsExcludingUsers(
                    Status.AVAILABLE,
                    blockedUserIds
            );
        }

        List<Image> allImages = imageRepository.findAllByProductIn(products);

        Map<Long, List<String>> imagesByProductId =
                allImages.stream()
                        .collect(Collectors.groupingBy(
                                img -> img.getProduct().getId(),
                                Collectors.mapping(Image::getImageUrl, Collectors.toList())
                        ));

        Set<Long> favouriteProductIds;
        try {
            favouriteProductIds =
                    favouriteRepository.findFavouriteProductIds(currentUser, products);
        } catch (Exception ignored) {
            favouriteProductIds = Set.of();
        }

        Set<Long> finalFavouriteProductIds = favouriteProductIds;
        return products.stream()
                .map(product ->
                        buildResponse(
                                product,
                                imagesByProductId.getOrDefault(product.getId(), List.of()),
                                finalFavouriteProductIds
                        )
                )
                .toList();
    }



    @Override
    public ProductDTO.Response getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        Users owner = product.getUser();

        if(usersBlockRepository.findByBlockerAndBlocked(owner, userService.getCurrentUser()).isPresent()){
            throw new BlockedByException("You are blocked.");
        }

        if(usersBlockRepository.findByBlockerAndBlocked(userService.getCurrentUser(), owner).isPresent()){
            throw new BlockedByException("You blocked this user.");
        }

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
        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new ConversationNotFoundException("Conversation not found"));
        Product product = productRepository.findById(conversation.getProduct().getId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        if(!product.getUser().getId().equals(sellerId)) {
            throw new UnauthorizedActionException("You can only mark your own products");
        }

        if(product.getStatus() == Status.SOLD) {
            throw new InvalidRequestFieldException("Ad is already marked as sold.");
        }

        Users buyer = conversation.getUser1().getId().equals(sellerId) ? conversation.getUser2() : conversation.getUser1();
        Transactions transaction = new  Transactions();
        transaction.setProduct(product);
        transaction.setBuyer(buyer);
        transaction.setSeller(userService.getCurrentUser());
        transactionRepository.save(transaction);
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
