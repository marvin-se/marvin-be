package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.data.dto.FavouriteDTO;
import com.marvin.campustrade.repository.ProductRepository;
import com.marvin.campustrade.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavouriteFacadeService {
    private final FavouriteService favouriteService;
    private final ProductRepository productRepository;

    @Transactional
    public FavouriteDTO addToFavourites(Long userId, Long productId) {
        FavouriteDTO dto = favouriteService.addToFavourites(userId, productId);
        productRepository.incrementFavouriteCount(productId);
        return dto;
    }

    @Transactional
    public void removeFromFavourites(Long userId, Long productId) {
        favouriteService.removeFromFavourites(userId, productId);
        productRepository.decrementFavouriteCount(productId);
    }
}
