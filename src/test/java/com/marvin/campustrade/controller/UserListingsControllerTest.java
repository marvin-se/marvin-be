package com.marvin.campustrade.controller;

import com.marvin.campustrade.data.dto.ProductDTO;
import com.marvin.campustrade.data.entity.Users;
import com.marvin.campustrade.service.ProductService;
import com.marvin.campustrade.service.UserService;
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
class UserListingsControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserListingsController userListingsController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userListingsController)
                .build();
    }

    @Test
    void getSellerProducts_returnsProductList() throws Exception {
        Users currentUser = new Users();
        currentUser.setId(10L);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(productService.getSellerProducts(5L, 10L))
                .thenReturn(List.of(
                        new ProductDTO.Response(),
                        new ProductDTO.Response()
                ));

        mockMvc.perform(get("/user/5/listings")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }
}
