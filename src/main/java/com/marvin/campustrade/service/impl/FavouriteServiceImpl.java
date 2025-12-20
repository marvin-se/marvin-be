package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.data.dto.AddFavouriteRequest;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavouriteServiceImpl implements FavouriteService {

    private final FavouriteRepository favouriteRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final FavouriteMapper favouriteMapper;

    // =========================
    // ADD FAVOURITE
    // =========================
    @Override
    @Transactional
    public FavouriteDTO addFavourite(AddFavouriteRequest request) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (favouriteRepository.existsByUserAndProduct(user, product)) {
            throw new RuntimeException("Product already in favourites");
        }

        Favourite favourite = Favourite.builder()
                .user(user)
                .product(product)
                .build();

        return favouriteMapper.toDTO(
                favouriteRepository.save(favourite)
        );
    }

    @Override
    @Transactional
    public void removeFromFavourites(Long productId) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Favourite favourite = favouriteRepository
                .findByUserAndProductId(user, productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Favourite not found"
                ));

        favouriteRepository.delete(favourite);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavouriteDTO> getUserFavourites() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return favouriteRepository.findAllByUser(user)
                .stream()
                .map(favouriteMapper::toDTO)
                .toList();
    }
}
