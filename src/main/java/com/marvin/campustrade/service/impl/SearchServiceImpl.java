package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.data.dto.*;
import com.marvin.campustrade.repository.UniversityRepository;
import com.marvin.campustrade.service.SearchService;

import com.marvin.campustrade.constants.Category;
import com.marvin.campustrade.constants.Status;
import com.marvin.campustrade.data.dto.SearchRequestDTO;
import com.marvin.campustrade.data.dto.SearchResponseDTO;

import com.marvin.campustrade.data.entity.Image;
import com.marvin.campustrade.data.entity.Product;
import com.marvin.campustrade.data.mapper.ProductMapper;
import com.marvin.campustrade.repository.ImageRepository;
import com.marvin.campustrade.repository.ProductSearchRepository;
import com.marvin.campustrade.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final ProductSearchRepository productSearchRepository;
    private final ProductMapper productMapper;
    private final ImageRepository imageRepository;
    private final UniversityRepository universityRepository;

    @Override
    @Transactional(readOnly = true)
    public SearchResponseDTO searchProducts(SearchRequestDTO request) {
        return executeSearch(request);
    }

    @Override
    @Transactional(readOnly = true)
    public SearchResponseDTO filterProducts(SearchRequestDTO request) {
        return executeSearch(request);
    }

    @Override
    public List<CategoryResponseDTO> getCategoryList() {
        return Arrays.stream(Category.values())
                .map(category -> new CategoryResponseDTO(
                        category,
                        formatCategoryName(category)
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<CampusResponseDTO> getCampusList() {
        return universityRepository.findAll()
                .stream()
                .map(u -> new CampusResponseDTO(u.getId(), u.getName()))
                .toList();
    }





    private SearchResponseDTO executeSearch(SearchRequestDTO request) {
        Sort sort = createSort(request.getSortBy(), request.getSortDirection());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<Product> productPage = productSearchRepository.searchProducts(
                request.getKeyword(),
                request.getCategory(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getUniversityId(),
                Status.AVAILABLE,  // Only show available products
                pageable
        );

        List<ProductDTO.Response> responses = productPage.getContent().stream()
                .map(this::mapProductToResponse)
                .collect(Collectors.toList());

        return SearchResponseDTO.builder()
                .products(responses)
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .currentPage(productPage.getNumber())
                .pageSize(productPage.getSize())
                .hasNext(productPage.hasNext())
                .hasPrevious(productPage.hasPrevious())
                .build();
    }


    private ProductDTO.Response mapProductToResponse(Product product) {
        ProductDTO.Response response = productMapper.toResponse(product);

        List<Image> images = imageRepository.findByProduct(product);
        List<String> imageUrls = images.stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());

        response.setImages(imageUrls);
        return response;
    }

    private Sort createSort(String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        String fieldName = switch (sortBy.toLowerCase()) {
            case "price" -> "price";
            case "title" -> "title";
            case "createdat", "created_at" -> "createdAt";
            default -> "createdAt";
        };

        return Sort.by(direction, fieldName);
    }


    private String formatCategoryName(Category category) {
        String name = category.name();
        return name.substring(0, 1).toUpperCase() +
                name.substring(1).toLowerCase();
    }


}
