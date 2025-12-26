package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.data.dto.UniversityResponseDTO;
import com.marvin.campustrade.data.entity.University;
import com.marvin.campustrade.data.mapper.UniversityMapper;
import com.marvin.campustrade.repository.UniversityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UniversityServiceImplTest {

    @Mock
    private UniversityRepository universityRepository;

    @Mock
    private UniversityMapper universityMapper;

    @InjectMocks
    private UniversityServiceImpl universityService;

    @Test
    void getAllUniversities_shouldReturnMappedUniversityList_sortedByName() {

        List<University> universities = List.of(
                new University(),
                new University()
        );

        List<UniversityResponseDTO> responseDTOs = List.of(
                new UniversityResponseDTO("ITU"),
                new UniversityResponseDTO("METU")
        );

        when(universityRepository.findAll(Sort.by("name")))
                .thenReturn(universities);

        when(universityMapper.toResponseList(universities))
                .thenReturn(responseDTOs);

        List<UniversityResponseDTO> result =
                universityService.getAllUniversities();


        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("ITU");
        assertThat(result.get(1).getName()).isEqualTo("METU");

        verify(universityRepository)
                .findAll(Sort.by("name"));

        verify(universityMapper)
                .toResponseList(universities);

        verifyNoMoreInteractions(universityRepository, universityMapper);
    }
}
