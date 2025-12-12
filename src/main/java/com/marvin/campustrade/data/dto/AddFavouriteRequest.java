package com.marvin.campustrade.data.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddFavouriteRequest {

    private Long userId;
    private Long productId;
}
