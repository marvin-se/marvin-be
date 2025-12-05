package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.data.dto.FavouriteDTO;
import com.marvin.campustrade.data.entity.Favourite;
import com.marvin.campustrade.data.entity.Product;
import com.marvin.campustrade.data.entity.Users;
import com.marvin.campustrade.data.mapper.FavouriteMapper;
import com.marvin.campustrade.repository.FavouriteRepository;
import com.marvin.campustrade.repository.ProductRepository;
import com.marvin.campustrade.repository.UserRepository;
import com.marvin.campustrade.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;


@Service
@RequiredArgsConstructor
public class FavouriteServiceImpl implements FavouriteService{

    private final FavouriteRepository favouriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final FavouriteMapper favouriteMapper;

    @Override
    public FavouriteDTO addToFavourites(Long userId, Long productId){

        if(favouriteRepository.existsByUserIdAndProductId(userId,productId)){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Product is favourite already"
            );
        }

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"user not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Favourite favourite = new Favourite();
        favourite.setUser(user);
        favourite.setProduct(product);

        Favourite saved = favouriteRepository.save(favourite);

        return favouriteMapper.toDTO(saved);

    }

    @Override
    public void removeFromFavourites(Long userId, Long productId){
        Favourite favourite = favouriteRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Favourite not found for userId=" + userId + " and productId=" + productId
                ));

        favouriteRepository.delete(favourite);
    }

    @Override
    public List<FavouriteDTO> getUserFavourites(Long userId){

        List<Favourite> favourites = favouriteRepository.findAllByUserId(userId);
        return favourites.stream().map(favouriteMapper::toDTO).toList();
    }

}