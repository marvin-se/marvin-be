package com.marvin.campustrade.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public  class FavouriteDTO {

    private Long id;
    private Long userId;
    private Long productId;

}