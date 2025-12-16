package com.marvin.campustrade.data.dto.user;

import com.marvin.campustrade.data.dto.ProductDTO;
import com.marvin.campustrade.data.dto.auth.UserResponse;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {
    private Long id;
    private ProductDTO.Response product;
    private UserResponse seller;
    private UserResponse buyer;
    private LocalDateTime createdAt;
}
