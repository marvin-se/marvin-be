package com.marvin.campustrade.data.dto;

import com.marvin.campustrade.constants.Category;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDTO {

    private Category category;
    private String displayName;
}
