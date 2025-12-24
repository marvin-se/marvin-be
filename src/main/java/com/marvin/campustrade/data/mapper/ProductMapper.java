package com.marvin.campustrade.data.mapper;

import com.marvin.campustrade.data.dto.ProductDTO;
import com.marvin.campustrade.data.entity.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // ---------- CREATE ----------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    Product toEntity(ProductDTO.CreateRequest request);

    // ---------- PUBLIC RESPONSE ----------
    @Mapping(target = "sellerId", source = "user.id")
    @Mapping(target = "universityName", source = "user.university.name")
    @Mapping(target = "images", ignore = true)

    // owner-only fields are NOT mapped by default
    @Mapping(target = "favouriteCount", ignore = true)
    @Mapping(target = "visitCount", ignore = true)
    ProductDTO.Response toResponse(Product product);

    // ---------- UPDATE ----------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(
            ProductDTO.UpdateRequest request,
            @MappingTarget Product product
    );

    // ---------- OWNER-ONLY METRICS ----------
    default void includeOwnerMetrics(
            Product product,
            @MappingTarget ProductDTO.Response response
    ) {
        response.setVisitCount(product.getVisitCount());
        response.setFavouriteCount(product.getFavouriteCount());
    }
}
