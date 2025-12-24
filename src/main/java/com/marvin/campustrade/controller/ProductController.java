package com.marvin.campustrade.controller;

import com.marvin.campustrade.data.dto.ImageDTO;
import com.marvin.campustrade.data.dto.ProductDTO;
import com.marvin.campustrade.service.AuthenticationService;
import com.marvin.campustrade.service.ImageService;
import com.marvin.campustrade.service.ProductService;
import com.marvin.campustrade.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/listings")
@RequiredArgsConstructor
public class ProductController {
    private final ImageService imageService;
    private final ProductService productService;
    private final UserService userService;

    @PostMapping("")
    public ResponseEntity<ProductDTO.Response> createProduct(
            @Valid @RequestBody ProductDTO.CreateRequest request
    ) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @GetMapping("")
    public ResponseEntity<List<ProductDTO.Response>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO.Response> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        Long sellerId = userService.getCurrentUser().getId();
        productService.deleteProduct(id, sellerId);
        return ResponseEntity.ok("Ad deleted successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO.Response> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO.UpdateRequest request
    ) {
        Long sellerId = userService.getCurrentUser().getId();
        return ResponseEntity.ok(
                productService.updateProduct(id, request, sellerId)
        );
    }

    @PutMapping("/{id}/mark-sold")
    public ResponseEntity<ProductDTO.Response> markAdAsSold(@PathVariable Long id) {
        Long sellerId = userService.getCurrentUser().getId();
        return ResponseEntity.ok(
                productService.markAsSold(id, sellerId)
        );
    }

    @PostMapping("/{id}/images/presign")
    public ResponseEntity<ImageDTO.PresignResponse> presignImage(
            @PathVariable Long id,
            @Valid @RequestBody ImageDTO.PresignRequest request)
    {
        return ResponseEntity.ok(
                imageService.presignUploads(id, request)
        );
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<Void> saveImages(
            @PathVariable Long id,
            @Valid @RequestBody ImageDTO.SaveImagesRequest request
    ) {
        productService.saveImages(id, request.getImageKeys());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<Void> replaceImages(
            @PathVariable Long id,
            @Valid @RequestBody ImageDTO.SaveImagesRequest request
    ) {
        productService.replaceImages(id, request.getImageKeys());
        return ResponseEntity.ok().build();
    }
}
