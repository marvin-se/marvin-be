package com.marvin.campustrade.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marvin.campustrade.data.dto.message.ConversationDTO;
import com.marvin.campustrade.data.dto.message.ConversationList;
import com.marvin.campustrade.security.AuthTokenFilter;
import com.marvin.campustrade.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = MessageController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = AuthTokenFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getConversationList_success() throws Exception {
        ConversationList response = new ConversationList();
        // populate if needed

        when(messageService.getConversationList())
                .thenReturn(response);

        mockMvc.perform(get("/messages"))
                .andExpect(status().isOk());

        verify(messageService).getConversationList();
    }

    @Test
    void getConversation_success() throws Exception {
        ConversationDTO response = new ConversationDTO();
        // populate fields if needed

        when(messageService.getConversation(5L, 10L))
                .thenReturn(response);

        mockMvc.perform(get("/messages/conversations/{otherUserId}/{productId}", 5L, 10L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(messageService).getConversation(5L, 10L);
    }

    @Test
    void deleteConversation_success() throws Exception {
        doNothing().when(messageService).deleteConversation(5L, 10L);

        mockMvc.perform(delete("/messages/conversations/{otherUserId}/{productId}", 5L, 10L))
                .andExpect(status().isNoContent());

        verify(messageService).deleteConversation(5L, 10L);
    }
}

