package com.marvin.campustrade.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marvin.campustrade.data.dto.ProfileImageDTO;
import com.marvin.campustrade.data.dto.auth.UserResponse;
import com.marvin.campustrade.data.dto.user.*;
import com.marvin.campustrade.security.AuthTokenFilter;
import com.marvin.campustrade.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = AuthTokenFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getCurrentProfile_success() throws Exception {
        UserResponse response = new UserResponse(
                1L,
                "Test User",
                "test@itu.edu.tr",
                "5551234567",
                10L,
                "ITU",
                "https://cdn.test/profile.jpg",
                LocalDateTime.now(),
                true
        );

        Mockito.when(userService.getCurrentProfile())
                .thenReturn(response);

        mockMvc.perform(get("/user/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@itu.edu.tr"))
                .andExpect(jsonPath("$.universityName").value("ITU"))
                .andExpect(jsonPath("$.isActive").value(true));
    }


    @Test
    void editProfile_success() throws Exception {
        EditProfileRequest request = new EditProfileRequest(
                "Updated Name",
                "5551234567",
                "New description"
        );

        UserResponse response = new UserResponse(
                1L,
                "Updated Name",
                "test@itu.edu.tr",
                "5551234567",
                10L,
                "ITU",
                null,
                LocalDateTime.now(),
                true
        );

        Mockito.when(userService.editProfile(Mockito.any()))
                .thenReturn(response);

        mockMvc.perform(put("/user/edit-profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated Name"))
                .andExpect(jsonPath("$.phoneNumber").value("5551234567"));
    }


    @Test
    void deleteProfile_success() throws Exception {
        Mockito.doNothing().when(userService).deleteProfile();

        mockMvc.perform(delete("/user/delete-profile"))
                .andExpect(status().isOk())
                .andExpect(content().string("Your profile has been deleted"));
    }

    @Test
    void getUser_success() throws Exception {
        ProfileResponse response = new ProfileResponse(
                "Test User",
                10L,
                "ITU",
                "https://cdn.test/profile.jpg",
                "Computer Engineering Student"
        );

        Mockito.when(userService.getUser("123"))
                .thenReturn(response);

        mockMvc.perform(get("/user/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Test User"))
                .andExpect(jsonPath("$.universityId").value(10))
                .andExpect(jsonPath("$.universityName").value("ITU"))
                .andExpect(jsonPath("$.profilePicUrl").value("https://cdn.test/profile.jpg"))
                .andExpect(jsonPath("$.description").value("Computer Engineering Student"));
    }


    @Test
    void blockUser_success() throws Exception {
        BlockResponse response = new BlockResponse("Blocked User");

        Mockito.when(userService.blockUser("123"))
                .thenReturn(response);

        mockMvc.perform(post("/user/123/block"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Blocked User"));

        Mockito.verify(userService).blockUser("123");
    }


    @Test
    void unblockUser_success() throws Exception {
        BlockResponse response = new BlockResponse("Unblocked User");

        Mockito.when(userService.unblockUser("123"))
                .thenReturn(response);

        mockMvc.perform(delete("/user/123/unblock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Unblocked User"));

        Mockito.verify(userService).unblockUser("123");
    }


    @Test
    void getSalesHistory_success() throws Exception {
        SalesResponseDTO response = new SalesResponseDTO();

        Mockito.when(userService.getSalesHistory())
                .thenReturn(response);

        mockMvc.perform(get("/user/sales"))
                .andExpect(status().isOk());
    }

    @Test
    void getPurchaseHistory_success() throws Exception {
        PurchaseResponseDTO response = new PurchaseResponseDTO();

        Mockito.when(userService.getPurchaseHistory())
                .thenReturn(response);

        mockMvc.perform(get("/user/purchases"))
                .andExpect(status().isOk());
    }

    @Test
    void getBlockedUsers_success() throws Exception {
        UserResponse user1 = new UserResponse(
                1L,
                "Blocked User 1",
                "blocked1@itu.edu.tr",
                null,
                10L,
                "ITU",
                null,
                LocalDateTime.now(),
                true
        );

        UserResponse user2 = new UserResponse(
                2L,
                "Blocked User 2",
                "blocked2@itu.edu.tr",
                null,
                10L,
                "ITU",
                null,
                LocalDateTime.now(),
                true
        );

        BlockListResponse response = BlockListResponse.builder()
                .userList(List.of(user1, user2))
                .numberOfBlocked(2)
                .build();

        Mockito.when(userService.getBlockList())
                .thenReturn(response);

        mockMvc.perform(get("/user/blocked"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfBlocked").value(2))
                .andExpect(jsonPath("$.userList").isArray())
                .andExpect(jsonPath("$.userList.length()").value(2))
                .andExpect(jsonPath("$.userList[0].fullName").value("Blocked User 1"))
                .andExpect(jsonPath("$.userList[1].email").value("blocked2@itu.edu.tr"));

        Mockito.verify(userService).getBlockList();
    }


}

