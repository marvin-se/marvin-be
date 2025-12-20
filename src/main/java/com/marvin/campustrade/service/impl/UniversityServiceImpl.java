package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.data.dto.UniversityResponseDTO;
import com.marvin.campustrade.data.mapper.UniversityMapper;
import com.marvin.campustrade.repository.UniversityRepository;
import com.marvin.campustrade.service.UniversityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UniversityServiceImpl implements UniversityService {
    private final UniversityRepository universityRepository;
    private final UniversityMapper universityMapper;

    @Override
    public List<UniversityResponseDTO> getAllUniversities(){
        return universityMapper.toResponseList(
                universityRepository.findAll(Sort.by("name"))
        );
    }
}
