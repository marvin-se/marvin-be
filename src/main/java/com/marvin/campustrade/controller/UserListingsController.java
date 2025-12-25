package com.marvin.campustrade.controller;

import com.marvin.campustrade.data.dto.ProductDTO;
import com.marvin.campustrade.service.ProductService;
import com.marvin.campustrade.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserListingsController {
    private final ProductService productService;
    private final UserService userService;

    @GetMapping("/{sellerId}/listings")
    public ResponseEntity<List<ProductDTO.Response>> getSellerProducts( @PathVariable Long sellerId) {
        Long currentUserId = userService.getCurrentUser().getId();
        return ResponseEntity.ok(
                productService.getSellerProducts(
                        sellerId,
                        currentUserId
                )
        );
    }

}
