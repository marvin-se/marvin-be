package com.marvin.campustrade.data.dto;

import lombok.*;


import java.util.List;
import com.marvin.campustrade.constants.Category;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SearchRequestDTO {
    private String keyword;
    private Category category;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Long universityId;
    private String universityName;


    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private Integer size = 20;

    @Builder.Default
    private String sortBy = "createdAt";

    @Builder.Default
    private String sortDirection = "DESC";
}
