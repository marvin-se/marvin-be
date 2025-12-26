package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.constants.Category;
import com.marvin.campustrade.constants.Status;
import com.marvin.campustrade.data.dto.*;
import com.marvin.campustrade.data.entity.Image;
import com.marvin.campustrade.data.entity.Product;
import com.marvin.campustrade.data.entity.University;
import com.marvin.campustrade.data.mapper.ProductMapper;
import com.marvin.campustrade.repository.ImageRepository;
import com.marvin.campustrade.repository.ProductSearchRepository;
import com.marvin.campustrade.repository.UniversityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {

    @Mock
    private ProductSearchRepository productSearchRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private UniversityRepository universityRepository;

    @InjectMocks
    private SearchServiceImpl searchService;

    @Test
    void searchProducts_shouldReturnPagedSearchResponse() {
        // GIVEN
        SearchRequestDTO request = new SearchRequestDTO();
        request.setKeyword("phone");
        request.setPage(0);
        request.setSize(10);
        request.setSortBy("price");
        request.setSortDirection("ASC");

        Product product = new Product();
        Image image = new Image();
        image.setImageUrl("img1.jpg");

        ProductDTO.Response productResponse = new ProductDTO.Response();
        productResponse.setTitle("Phone");

        Page<Product> productPage = new PageImpl<>(
                List.of(product),
                PageRequest.of(0, 10, Sort.by("price")),
                1
        );

        when(productSearchRepository.searchProducts(
                any(), any(), any(), any(), any(),
                eq(Status.AVAILABLE),
                any(Pageable.class)
        )).thenReturn(productPage);

        when(productMapper.toResponse(product))
                .thenReturn(productResponse);

        when(imageRepository.findByProduct(product))
                .thenReturn(List.of(image));

        // WHEN
        SearchResponseDTO result =
                searchService.searchProducts(request);

        // THEN
        assertThat(result.getProducts()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getCurrentPage()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(10);
        assertThat(result.getHasNext()).isFalse();
        assertThat(result.getHasPrevious()).isFalse();

        assertThat(result.getProducts().get(0).getImages())
                .containsExactly("img1.jpg");

        verify(productSearchRepository).searchProducts(
                eq("phone"),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(Status.AVAILABLE),
                any(Pageable.class)
        );
    }

    @Test
    void filterProducts_shouldDelegateToSameSearchLogic() {
        // GIVEN
        SearchRequestDTO request = new SearchRequestDTO();
        request.setPage(0);
        request.setSize(5);
        request.setSortBy("createdAt");
        request.setSortDirection("DESC");

        Page<Product> emptyPage = Page.empty();

        when(productSearchRepository.searchProducts(
                any(), any(), any(), any(), any(),
                eq(Status.AVAILABLE),
                any(Pageable.class)
        )).thenReturn(emptyPage);

        // WHEN
        SearchResponseDTO response =
                searchService.filterProducts(request);

        // THEN
        assertThat(response.getProducts()).isEmpty();
        assertThat(response.getTotalElements()).isZero();

        verify(productSearchRepository, times(1))
                .searchProducts(any(), any(), any(), any(), any(),
                        eq(Status.AVAILABLE),
                        any(Pageable.class));
    }

    @Test
    void getCategoryList_shouldReturnAllCategoriesWithFormattedNames() {
        // WHEN
        List<CategoryResponseDTO> categories =
                searchService.getCategoryList();

        // THEN
        assertThat(categories).isNotEmpty();
        assertThat(categories)
                .extracting(CategoryResponseDTO::getCategory)
                .contains(Category.values());

        assertThat(categories.get(0).getDisplayName())
                .isNotBlank();
    }


    @Test
    void getCampusList_shouldMapUniversitiesToCampusDTOs() {
        // GIVEN
        University u1 = new University();
        u1.setId(1L);
        u1.setName("ITU");

        University u2 = new University();
        u2.setId(2L);
        u2.setName("METU");

        when(universityRepository.findAll())
                .thenReturn(List.of(u1, u2));

        // WHEN
        List<CampusResponseDTO> campuses =
                searchService.getCampusList();

        // THEN
        assertThat(campuses).hasSize(2);
        assertThat(campuses.get(0).getName()).isEqualTo("ITU");
        assertThat(campuses.get(1).getName()).isEqualTo("METU");

        verify(universityRepository).findAll();
    }
}
