package com.marvin.campustrade.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marvin.campustrade.data.dto.AddFavouriteRequest;
import com.marvin.campustrade.data.dto.FavouriteDTO;
import com.marvin.campustrade.service.FavouriteService;
import com.marvin.campustrade.service.impl.FavouriteFacadeService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FavouriteControllerTest {

    @Mock
    private FavouriteService favouriteService;

    @Mock
    private FavouriteFacadeService favouriteFacadeService;

    @InjectMocks
    private FavouriteController favouriteController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(favouriteController)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void addFavourite_returnsCreatedFavourite() throws Exception {
        AddFavouriteRequest request = new AddFavouriteRequest();
        FavouriteDTO response = new FavouriteDTO();

        when(favouriteFacadeService.addFavourite(any()))
                .thenReturn(response);

        mockMvc.perform(post("/favourites/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void removeFromFavourites_returnsNoContent() throws Exception {
        doNothing().when(favouriteFacadeService)
                .removeFromFavourites(1L);

        mockMvc.perform(delete("/favourites/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getUserFavourites_returnsList() throws Exception {
        when(favouriteService.getUserFavourites())
                .thenReturn(List.of(
                        new FavouriteDTO(),
                        new FavouriteDTO()
                ));

        mockMvc.perform(get("/favourites/getAll")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }
}
