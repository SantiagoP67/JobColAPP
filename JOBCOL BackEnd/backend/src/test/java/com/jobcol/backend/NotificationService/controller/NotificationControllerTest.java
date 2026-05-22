package com.jobcol.backend.NotificationService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobcol.backend.NotificationService.model.NotificationType;
import com.jobcol.backend.NotificationService.service.NotificationService;
import com.jobcol.backend.shared.dto.NotificationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean  private NotificationService notificationService;
    @Autowired private ObjectMapper objectMapper;

    private NotificationDTO notificationDTO;

    @BeforeEach
    void setUp() {
        notificationDTO = NotificationDTO.builder()
                .id(1L)
                .title("Título test")
                .message("Mensaje test")
                .type(NotificationType.SEGURIDAD)
                .read(false)
                .createdAt(LocalDateTime.now())
                .userId(1L)
                .build();
    }

    @Test
    void createNotification_shouldReturn200() throws Exception {
        when(notificationService.createNotification(1L, "Título test", "Mensaje test", NotificationType.SEGURIDAD))
                .thenReturn(notificationDTO);

        mockMvc.perform(post("/notifications/create")
                        .param("userId", "1")
                        .param("title", "Título test")
                        .param("message", "Mensaje test")
                        .param("type", "SEGURIDAD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Título test"));
    }

    @Test
    void createNotificationsBatch_shouldReturn200() throws Exception {
        BatchNotificationRequest request = new BatchNotificationRequest();
        request.setUserIds(List.of(1L, 2L));
        request.setTitle("Batch title");
        request.setMessage("Batch message");
        request.setType(NotificationType.INFO);

        doNothing().when(notificationService).createNotificationsBatch(any(), any(), any(), any());

        mockMvc.perform(post("/notifications/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Notifications sent successfully"));
    }

    @Test
    void getUserNotifications_shouldReturn200() throws Exception {
        when(notificationService.getUserNotifications(1L)).thenReturn(List.of(notificationDTO));

        mockMvc.perform(get("/notifications/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Título test"));
    }

    @Test
    void getUnreadNotifications_shouldReturn200() throws Exception {
        when(notificationService.getUnreadNotifications(1L)).thenReturn(List.of(notificationDTO));

        mockMvc.perform(get("/notifications/user/1/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].read").value(false));
    }

    @Test
    void countUnread_shouldReturn200() throws Exception {
        when(notificationService.countUnread(1L)).thenReturn(5L);

        mockMvc.perform(get("/notifications/user/1/count-unread"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void markAsRead_shouldReturn200() throws Exception {
        when(notificationService.markAsRead(1L)).thenReturn(notificationDTO);

        mockMvc.perform(put("/notifications/1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void markAllAsRead_shouldReturn200() throws Exception {
        doNothing().when(notificationService).markAllAsRead(1L);

        mockMvc.perform(put("/notifications/user/1/read-all"))
                .andExpect(status().isOk())
                .andExpect(content().string("All notifications marked as read"));
    }

    @Test
    void deleteNotification_shouldReturn200() throws Exception {
        doNothing().when(notificationService).deleteNotification(1L);

        mockMvc.perform(delete("/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification deleted"));
    }
}