package com.marvin.campustrade.data.dto.user;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesResponseDTO {
    private List<TransactionDTO> transactions;
}
