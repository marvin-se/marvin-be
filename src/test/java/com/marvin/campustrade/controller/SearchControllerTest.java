package com.marvin.campustrade.controller;

import com.marvin.campustrade.constants.Category;
import com.marvin.campustrade.data.dto.*;
import com.marvin.campustrade.security.AuthTokenFilter;
import com.marvin.campustrade.service.SearchService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = SearchController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = AuthTokenFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SearchService searchService;

    // ----------------------------------------------------
    // /search/listings
    // ----------------------------------------------------
    @Test
    void searchListings_shouldReturnSearchResponse() throws Exception {
        SearchResponseDTO response = SearchResponseDTO.builder()
                .products(List.of())
                .totalElements(0L)
                .totalPages(0)
                .currentPage(0)
                .pageSize(10)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        Mockito.when(searchService.searchProducts(Mockito.any(SearchRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(get("/search/listings")
                        .param("keyword", "phone")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products").isArray())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false));

        Mockito.verify(searchService)
                .searchProducts(Mockito.any(SearchRequestDTO.class));
    }

    @Test
    void searchListings_withoutParams_shouldStillReturnResponse() throws Exception {
        SearchResponseDTO response = SearchResponseDTO.builder()
                .products(List.of())
                .totalElements(0L)
                .totalPages(0)
                .currentPage(0)
                .pageSize(10)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        Mockito.when(searchService.searchProducts(Mockito.any(SearchRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(get("/search/listings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products").isArray());

        Mockito.verify(searchService)
                .searchProducts(Mockito.any(SearchRequestDTO.class));
    }

    @Test
    void searchListings_shouldBindKeywordCorrectly() throws Exception {
        Mockito.when(searchService.searchProducts(Mockito.any()))
                .thenReturn(SearchResponseDTO.builder()
                        .products(List.of())
                        .build());

        mockMvc.perform(get("/search/listings")
                .param("keyword", "laptop"));

        Mockito.verify(searchService)
                .searchProducts(Mockito.argThat(req ->
                        "laptop".equals(req.getKeyword())
                ));
    }

    @Test
    void filterListings_withoutFilters_shouldReturnResponse() throws Exception {
        SearchResponseDTO response = SearchResponseDTO.builder()
                .products(List.of())
                .totalElements(0L)
                .build();

        Mockito.when(searchService.filterProducts(Mockito.any()))
                .thenReturn(response);

        mockMvc.perform(get("/search/filter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));

        Mockito.verify(searchService)
                .filterProducts(Mockito.any(SearchRequestDTO.class));
    }

    @Test
    void getCategoryList_shouldReturnEmptyList() throws Exception {
        Mockito.when(searchService.getCategoryList())
                .thenReturn(List.of());

        mockMvc.perform(get("/search/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        Mockito.verify(searchService).getCategoryList();
    }

    @Test
    void getCampusList_shouldReturnEmptyList() throws Exception {
        Mockito.when(searchService.getCampusList())
                .thenReturn(List.of());

        mockMvc.perform(get("/search/campuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        Mockito.verify(searchService).getCampusList();
    }

    @Test
    void searchListings_postShouldFail() throws Exception {
        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/search/listings")
        ).andExpect(status().isMethodNotAllowed());
    }

    @Test
    void searchListings_shouldReturnJson() throws Exception {
        Mockito.when(searchService.searchProducts(Mockito.any()))
                .thenReturn(SearchResponseDTO.builder().products(List.of()).build());

        mockMvc.perform(get("/search/listings"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }

    @Test
    void filterListings_shouldReturnFilteredResults() throws Exception {
        SearchResponseDTO response = SearchResponseDTO.builder()
                .products(List.of())
                .totalElements(1L)
                .totalPages(1)
                .currentPage(0)
                .pageSize(10)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        Mockito.when(searchService.filterProducts(Mockito.any(SearchRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(get("/search/filter")
                        .param("category", "ELECTRONICS")
                        .param("campusId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.hasNext").value(false));

        Mockito.verify(searchService)
                .filterProducts(Mockito.any(SearchRequestDTO.class));
    }

    // ----------------------------------------------------
    // /search/categories
    // ----------------------------------------------------
    @Test
    void getCategoryList_shouldReturnCategories() throws Exception {
        CategoryResponseDTO category = new CategoryResponseDTO(
                Category.ELECTRONICS,
                "Electronics"
        );

        Mockito.when(searchService.getCategoryList())
                .thenReturn(List.of(category));

        mockMvc.perform(get("/search/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("ELECTRONICS"))
                .andExpect(jsonPath("$[0].displayName").value("Electronics"));

        Mockito.verify(searchService).getCategoryList();
    }

    // ----------------------------------------------------
    // /search/campuses
    // ----------------------------------------------------
    @Test
    void getCampusList_shouldReturnCampuses() throws Exception {
        CampusResponseDTO campus = new CampusResponseDTO(
                1L,
                "ITU"
        );

        Mockito.when(searchService.getCampusList())
                .thenReturn(List.of(campus));

        mockMvc.perform(get("/search/campuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("ITU"));

        Mockito.verify(searchService).getCampusList();
    }
}
