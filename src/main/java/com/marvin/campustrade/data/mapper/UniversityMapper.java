package com.marvin.campustrade.data.mapper;

import com.marvin.campustrade.data.dto.UniversityResponseDTO;
import com.marvin.campustrade.data.entity.University;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UniversityMapper {

    UniversityResponseDTO toResponse(University university);

    List<UniversityResponseDTO> toResponseList(List<University> universities);
}

