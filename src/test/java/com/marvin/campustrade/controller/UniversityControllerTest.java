package com.marvin.campustrade.controller;

import com.marvin.campustrade.data.dto.UniversityResponseDTO;
import com.marvin.campustrade.service.UniversityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UniversityControllerTest {

    @Mock
    private UniversityService universityService;

    @InjectMocks
    private UniversityController universityController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(universityController)
                .build();
    }

    @Test
    void getAllUniversities_returnsListOfUniversityNames() throws Exception {
        // Arrange
        when(universityService.getAllUniversities())
                .thenReturn(List.of(
                        new UniversityResponseDTO("Harvard University"),
                        new UniversityResponseDTO("MIT")
                ));

        // Act + Assert
        mockMvc.perform(get("/universities")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Harvard University"))
                .andExpect(jsonPath("$[1].name").value("MIT"));
    }
}
