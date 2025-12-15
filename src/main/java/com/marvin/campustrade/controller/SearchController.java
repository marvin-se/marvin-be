package com.marvin.campustrade.controller;

import com.marvin.campustrade.data.dto.*;
import com.marvin.campustrade.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/listings")
    public ResponseEntity<SearchResponseDTO> searchListings(@ModelAttribute SearchRequestDTO request) {

        System.out.println(">>> KEYWORD = " + request.getKeyword());

        return ResponseEntity.ok(searchService.searchProducts(request));
    }


    @GetMapping("/filter")
    public ResponseEntity<SearchResponseDTO> filterListings(
            @ModelAttribute SearchRequestDTO request
    ) {


        return ResponseEntity.ok(searchService.filterProducts(request));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoryList() {
        return ResponseEntity.ok(searchService.getCategoryList());
    }

    @GetMapping("/campuses")
    public ResponseEntity<List<CampusResponseDTO>> getCampusList() {
        return ResponseEntity.ok(searchService.getCampusList());
    }
}