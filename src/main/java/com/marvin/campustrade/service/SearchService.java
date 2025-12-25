package com.marvin.campustrade.service;

import com.marvin.campustrade.data.dto.CampusResponseDTO;
import com.marvin.campustrade.data.dto.SearchRequestDTO;
import com.marvin.campustrade.data.dto.CategoryResponseDTO;
import com.marvin.campustrade.data.dto.SearchResponseDTO;


import java.util.List;

public interface SearchService {

    SearchResponseDTO searchProducts(SearchRequestDTO request);

    SearchResponseDTO filterProducts(SearchRequestDTO request);

    List<CategoryResponseDTO> getCategoryList();

    List<CampusResponseDTO> getCampusList();

}