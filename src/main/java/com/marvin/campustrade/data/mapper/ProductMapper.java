package com.marvin.campustrade.data.mapper;

import com.marvin.campustrade.data.dto.ProductDTO;
import com.marvin.campustrade.data.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)    // set manually from auth
    Product toEntity(ProductDTO.CreateRequest request);

    @Mapping(target = "universityName", source = "user.university.name")
    @Mapping(target = "images",  ignore = true)
    ProductDTO.Response toResponse(Product product);
}
