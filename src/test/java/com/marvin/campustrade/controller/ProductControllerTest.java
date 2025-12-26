package com.marvin.campustrade.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marvin.campustrade.constants.Category;
import com.marvin.campustrade.data.dto.ImageDTO;
import com.marvin.campustrade.data.dto.ProductDTO;
import com.marvin.campustrade.data.entity.Users;
import com.marvin.campustrade.service.ImageService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private ImageService imageService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(productController)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createProduct_returnsCreatedProduct() throws Exception {
        ProductDTO.CreateRequest request = new ProductDTO.CreateRequest();
        request.setTitle("Laptop");
        request.setPrice(BigDecimal.valueOf(1500));
        request.setCategory(Category.ELECTRONICS);

        ProductDTO.Response response = new ProductDTO.Response();

        when(productService.createProduct(any()))
                .thenReturn(response);

        mockMvc.perform(post("/listings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void publishProduct_returnsOk() throws Exception {
        doNothing().when(productService).publishProduct(1L);

        mockMvc.perform(put("/listings/1/publish"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllProducts_returnsList() throws Exception {
        when(productService.getAllProducts())
                .thenReturn(List.of(
                        new ProductDTO.Response(),
                        new ProductDTO.Response()
                ));

        mockMvc.perform(get("/listings")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void getProduct_returnsProduct() throws Exception {
        when(productService.getProductById(1L))
                .thenReturn(new ProductDTO.Response());

        mockMvc.perform(get("/listings/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteProduct_returnsSuccessMessage() throws Exception {
        Users user = new Users();
        user.setId(10L);

        when(userService.getCurrentUser()).thenReturn(user);
        doNothing().when(productService).deleteProduct(1L, 10L);

        mockMvc.perform(delete("/listings/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ad deleted successfully"));
    }

    @Test
    void updateProduct_returnsUpdatedProduct() throws Exception {
        Users user = new Users();
        user.setId(10L);

        ProductDTO.UpdateRequest request = new ProductDTO.UpdateRequest();
        ProductDTO.Response response = new ProductDTO.Response();

        when(userService.getCurrentUser()).thenReturn(user);
        when(productService.updateProduct(eq(1L), any(), eq(10L)))
                .thenReturn(response);

        mockMvc.perform(put("/listings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void markAdAsSold_returnsUpdatedProduct() throws Exception {
        Users user = new Users();
        user.setId(10L);

        when(userService.getCurrentUser()).thenReturn(user);
        when(productService.markAsSold(1L, 10L))
                .thenReturn(new ProductDTO.Response());

        mockMvc.perform(put("/listings/1/mark-sold"))
                .andExpect(status().isOk());
    }

    @Test
    void presignImage_returnsPresignResponse() throws Exception {
        ImageDTO.ImageItem imageItem =
                new ImageDTO.ImageItem(
                        "photo.jpg",
                        "image/jpeg"
                );

        ImageDTO.PresignRequest request =
                new ImageDTO.PresignRequest(
                        List.of(imageItem)
                );

        ImageDTO.PresignResponse response =
                new ImageDTO.PresignResponse(
                        List.of(
                                new ImageDTO.PresignedImage(
                                        "products/1/photo.jpg",
                                        "https://signed-upload-url"
                                )
                        )
                );

        when(imageService.presignUploads(eq(1L), any()))
                .thenReturn(response);

        mockMvc.perform(post("/listings/1/images/presign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.images.size()").value(1))
                .andExpect(jsonPath("$.images[0].key")
                        .value("products/1/photo.jpg"));
    }

    @Test
    void attachImages_returnsOk() throws Exception {
        ImageDTO.SaveImagesRequest request =
                new ImageDTO.SaveImagesRequest(List.of("img1", "img2"));

        doNothing().when(productService)
                .saveImages(1L, request.getImageKeys());

        mockMvc.perform(post("/listings/1/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void getImages_returnsImageList() throws Exception {
        ImageDTO.ImageResponse img1 = new ImageDTO.ImageResponse(
                "img1",
                "https://signed-url-1"
        );

        ImageDTO.ImageResponse img2 = new ImageDTO.ImageResponse(
                "img2",
                "https://signed-url-2"
        );

        ImageDTO.ImageListResponse response =
                new ImageDTO.ImageListResponse(List.of(img1, img2));

        when(imageService.getImagesWithPresignedUrls(1L))
                .thenReturn(response);

        mockMvc.perform(get("/listings/1/images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.images.size()").value(2));
    }

    @Test
    void deleteImages_returnsNoContent() throws Exception {
        doNothing().when(imageService)
                .deleteImage(1L, "image-key");

        mockMvc.perform(delete("/listings/1/images")
                        .param("imageKey", "image-key"))
                .andExpect(status().isNoContent());
    }
}
