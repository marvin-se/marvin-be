package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.data.dto.AddFavouriteRequest;
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
    public FavouriteDTO addFavourite(AddFavouriteRequest request) {
        FavouriteDTO dto = favouriteService.addFavourite(request);
        productRepository.incrementFavouriteCount(request.getProductId());
        return dto;
    }

    @Transactional
    public void removeFromFavourites(Long productId) {
        favouriteService.removeFromFavourites(productId);
        productRepository.decrementFavouriteCount(productId);
    }
}
