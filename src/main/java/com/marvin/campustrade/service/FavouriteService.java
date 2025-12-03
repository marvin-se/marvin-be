package com.marvin.campustrade.service;

import com.marvin.campustrade.data.dto.FavouriteDTO;
import java.util.List;

public  interface FavouriteService {

        FavouriteDTO addToFavourites(Long userId, Long productId);

        void removeFromFavourites(Long userId, Long productId);

        List<FavouriteDTO> getUserFavourites(Long userıd);


}