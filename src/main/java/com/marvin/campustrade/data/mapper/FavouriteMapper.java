package com.marvin.campustrade.data.mapper;


import com.marvin.campustrade.data.dto.FavouriteDTO;
import com.marvin.campustrade.data.entity.Favourite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface FavouriteMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "productId", source = "product.id")
    FavouriteDTO toDTO(Favourite favourite);

}