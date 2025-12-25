package com.marvin.campustrade.service;

import com.marvin.campustrade.data.dto.AddFavouriteRequest;
import com.marvin.campustrade.data.dto.FavouriteDTO;

import java.util.List;

public interface FavouriteService {

    FavouriteDTO addFavourite(AddFavouriteRequest request);

    void removeFromFavourites(Long productId);
    List<FavouriteDTO> getUserFavourites();

}
