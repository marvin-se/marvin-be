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
@RequestMapping("/favourites")
@RequiredArgsConstructor
public class FavouriteController {
    private final FavouriteService favouriteService;

    @PostMapping("/add")
    public ResponseEntity<FavouriteDTO> addFavourite(
            @RequestBody AddFavouriteRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(favouriteService.addFavourite(request));
    }


    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFromFavourites(@PathVariable Long productId) {
        favouriteService.removeFromFavourites(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<FavouriteDTO>> getUserFavourites() {
        return ResponseEntity.ok(favouriteService.getUserFavourites());
    }

}