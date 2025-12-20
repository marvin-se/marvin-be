package com.marvin.campustrade.controller;

import com.marvin.campustrade.data.dto.UniversityResponseDTO;
import com.marvin.campustrade.service.UniversityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/universities")
@RequiredArgsConstructor
public class UniversityController {
    private final UniversityService universityService;

    @GetMapping("")
    public ResponseEntity<List<UniversityResponseDTO>> getAllUniversities() {
        return ResponseEntity.ok(universityService.getAllUniversities());
    }

}
