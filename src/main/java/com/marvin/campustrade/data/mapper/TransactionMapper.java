package com.marvin.campustrade.data.mapper;

import com.marvin.campustrade.data.dto.user.TransactionDTO;
import com.marvin.campustrade.data.entity.Transactions;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, UserMapper.class})
public interface TransactionMapper {

    TransactionDTO toDto(Transactions transaction);

    List<TransactionDTO> toDtoList(List<Transactions> transactions);
}
