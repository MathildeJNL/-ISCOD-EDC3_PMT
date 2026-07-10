package com.codesolutions.pmt.web;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PmtApiIntegrationTest {
    private static final String PASSWORD = "Password123!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void projectTaskNotificationAndHistoryLifecycle() throws Exception {
        long suffix = System.nanoTime();
        String adminEmail = "admin-" + suffix + "@example.com";
        String memberEmail = "member-" + suffix + "@example.com";
        String observerEmail = "observer-" + suffix + "@example.com";

        long adminId = register("admin" + suffix, adminEmail);
        long memberId = register("member" + suffix, memberEmail);
        long observerId = register("observer" + suffix, observerEmail);

        login(adminEmail);
        invalidLogin(adminEmail);

        long projectId = createProject(adminId);
        long memberMembershipId = invite(projectId, adminId, memberEmail, "MEMBER");
        long observerMembershipId = invite(projectId, adminId, observerEmail, "OBSERVER");

        mockMvc.perform(patch("/api/projects/{projectId}/members/{memberId}/role", projectId, memberMembershipId)
                        .header("X-User-Id", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("role", "MEMBER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("MEMBER"));

        mockMvc.perform(post("/api/projects/{projectId}/tasks", projectId)
                        .header("X-User-Id", observerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(taskPayload(observerId))))
                .andExpect(status().isForbidden());

        long taskId = createTask(projectId, memberId, observerId);

        mockMvc.perform(get("/api/projects").header("X-User-Id", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));

        mockMvc.perform(get("/api/tasks/{taskId}", taskId).header("X-User-Id", observerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId));

        mockMvc.perform(get("/api/projects/{projectId}/tasks", projectId).header("X-User-Id", observerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));

        mockMvc.perform(patch("/api/tasks/{taskId}", taskId)
                        .header("X-User-Id", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", "IN_PROGRESS", "priority", "HIGH"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        mockMvc.perform(get("/api/projects/{projectId}/tasks", projectId)
                        .header("X-User-Id", observerId)
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));

        mockMvc.perform(patch("/api/tasks/{taskId}", taskId)
                        .header("X-User-Id", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("title", "Implement lifecycle test updated"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Implement lifecycle test updated"));

        mockMvc.perform(patch("/api/tasks/{taskId}", taskId)
                        .header("X-User-Id", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of())))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/tasks/{taskId}/assignments", taskId)
                        .header("X-User-Id", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("assigneeIds", List.of()))))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/tasks/{taskId}/assignments", taskId)
                        .header("X-User-Id", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("assigneeIds", List.of(observerId)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignees[0].id").value(observerId));

        mockMvc.perform(get("/api/tasks/{taskId}/history", taskId).header("X-User-Id", observerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(2)));

        MvcResult notifications = mockMvc.perform(get("/api/notifications").header("X-User-Id", observerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)))
                .andReturn();
        long notificationId = objectMapper.readTree(notifications.getResponse().getContentAsString())
                .get(0)
                .get("id")
                .asLong();

        mockMvc.perform(patch("/api/notifications/{notificationId}/read", notificationId)
                        .header("X-User-Id", observerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.readAt").exists());

        mockMvc.perform(patch("/api/projects/{projectId}/members/{memberId}/role", projectId, observerMembershipId)
                        .header("X-User-Id", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("role", "MEMBER"))))
                .andExpect(status().isForbidden());
    }

    private long register(String username, String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "username", username,
                                "email", email,
                                "password", PASSWORD
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.email").value(email))
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("user").get("id").asLong();
    }

    private void login(String email) throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "password", PASSWORD))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString());
    }

    private void invalidLogin(String email) throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "password", "bad-password"))))
                .andExpect(status().isForbidden());
    }

    private long createProject(long adminId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/projects")
                        .header("X-User-Id", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "name", "PMT integration",
                                "description", "Validated from MockMvc",
                                "startDate", LocalDate.now().toString()
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.members[0].role").value("ADMIN"))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private long invite(long projectId, long adminId, String email, String role) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/projects/{projectId}/members", projectId)
                        .header("X-User-Id", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "role", role))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value(role))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private long createTask(long projectId, long memberId, long observerId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/projects/{projectId}/tasks", projectId)
                        .header("X-User-Id", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(taskPayload(observerId))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.assignees[0].id").value(observerId))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private Map<String, Object> taskPayload(long assigneeId) {
        return Map.of(
                "title", "Implement lifecycle test",
                "description", "Create a task and validate assignment notification",
                "dueDate", LocalDate.now().plusDays(5).toString(),
                "priority", "HIGH",
                "assigneeIds", List.of(assigneeId)
        );
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }
}
