package com.marvin.campustrade.controller;


import com.marvin.campustrade.data.dto.AddFavouriteRequest;
import com.marvin.campustrade.data.dto.FavouriteDTO;
import com.marvin.campustrade.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/favourites")
@RequiredArgsConstructor
public class FavouriteController {
    private final FavouriteService favouriteService;

    @PostMapping("/{userId}")
    public ResponseEntity<FavouriteDTO> addToFavourites(
            @PathVariable Long userId,
            @RequestBody AddFavouriteRequest request
    ){
        FavouriteDTO dto = favouriteService.addToFavourites(userId, request.getProductId());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping("/{userId}/{productId}")
    public ResponseEntity<Void> removeFromFavourites(
            @PathVariable Long userId,
            @PathVariable Long productId
    ){
        favouriteService.removeFromFavourites(userId, productId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{userId}")
    public ResponseEntity<List<FavouriteDTO>> getUserFavourites(
            @PathVariable Long userId
    ){
        return ResponseEntity.ok(favouriteService.getUserFavourites(userId));
    }
}