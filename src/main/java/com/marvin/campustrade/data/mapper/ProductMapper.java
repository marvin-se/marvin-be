package com.marvin.campustrade.data.mapper;

import com.marvin.campustrade.data.dto.ProductDTO;
import com.marvin.campustrade.data.entity.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)    // set manually from auth
    Product toEntity(ProductDTO.CreateRequest request);

    @Mapping(target = "universityName", source = "user.university.name")
    @Mapping(target = "images",  ignore = true)
    @Mapping(target = "favoriteCount",  ignore = true)
    @Mapping(target = "visitCount",  ignore = true)
    ProductDTO.Response toResponse(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(
            ProductDTO.UpdateRequest request,
            @MappingTarget Product product
    );

    @AfterMapping
    default void includeOwnerMetrics(
            Product product,
            @MappingTarget ProductDTO.Response response
    ) {
        //response.setVisitCount(product.getVisitCount());
        //response.setFavoriteCount(product.getFavoriteCount());
    }
}
