package com.marvin.campustrade.data.dto;

import com.marvin.campustrade.constants.Category;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

public class ProductDTO {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "Title is required")
        private String title;

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        private String description;

        @NotBlank(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
        private BigDecimal price;

        @NotBlank(message = "Category is required")
        private Category category;

        @Size(min = 1, message = "At least one image must be provided")
        private List<String> images;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private BigDecimal price;
        private Category category;
        private String universityName;
        private List<String> images;
    }

}
