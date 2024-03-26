package com.allitov.tasktracker.integration;

import com.allitov.tasktracker.model.entity.RoleType;
import com.allitov.tasktracker.model.entity.Task;
import com.allitov.tasktracker.model.entity.User;
import com.allitov.tasktracker.model.repository.TaskRepository;
import com.allitov.tasktracker.model.repository.UserRepository;
import com.allitov.tasktracker.web.dto.request.ChangeTaskRequest;
import com.allitov.tasktracker.web.dto.request.CreateTaskRequest;
import com.allitov.testutils.EnableTestcontainers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@EnableTestcontainers
@AutoConfigureWebTestClient
public class TaskControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void beforeEach() {
        userRepository.saveAll(
                List.of(
                        User.builder()
                                .id("1")
                                .username("user1")
                                .email("email@email.com")
                                .password("1")
                                .roles(Set.of(RoleType.USER))
                                .build(),
                        User.builder()
                                .id("2")
                                .username("user2")
                                .email("email@email.com")
                                .password("2")
                                .roles(Set.of(RoleType.MANAGER, RoleType.USER))
                                .build()
                )
        ).blockLast();

        taskRepository.saveAll(
                List.of(
                        Task.builder()
                                .id("1")
                                .name("task1")
                                .description("description")
                                .createdAt(Instant.parse("1970-01-01T00:00:00Z"))
                                .updatedAt(Instant.parse("1970-01-01T00:00:00Z"))
                                .status(Task.TaskStatus.TODO)
                                .authorId("1")
                                .assigneeId("1")
                                .observerIds(Set.of("1"))
                                .build(),
                        Task.builder()
                                .id("2")
                                .name("task2")
                                .description("description")
                                .createdAt(Instant.parse("1970-01-01T00:00:00Z"))
                                .updatedAt(Instant.parse("1970-01-01T00:00:00Z"))
                                .status(Task.TaskStatus.DONE)
                                .authorId("2")
                                .assigneeId("2")
                                .observerIds(Collections.emptySet())
                                .build()
                )
        ).blockLast();
    }

    @AfterEach
    public void afterEach() {
        taskRepository.deleteAll().block();
        userRepository.deleteAll().block();
    }

    @Test
    @DisplayName("Test getAll() status 200")
    @WithMockUser(authorities = {"USER"})
    public void givenRoleUser_whenGetAll_thenTaskListResponse() {
        webTestClient.get().uri("/api/v2/task")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'tasks': [" +
                        "{'id': '1', " +
                        "'name': 'task1', " +
                        "'description': 'description', " +
                        "'createdAt': '1970-01-01T00:00:00Z', " +
                        "'updatedAt': '1970-01-01T00:00:00Z', " +
                        "'status': 'TODO', " +
                        "'author': {'id': '1', 'username': 'user1', 'email': 'email@email.com', 'roles': ['USER']}, " +
                        "'assignee': {'id': '1', 'username': 'user1', 'email': 'email@email.com', 'roles': ['USER']}, " +
                        "'observers': [{'id': '1', 'username': 'user1', 'email': 'email@email.com', 'roles': ['USER']}]}, " +
                        "{'id': '2', " +
                        "'name': 'task2', " +
                        "'description': 'description', " +
                        "'createdAt': '1970-01-01T00:00:00Z', " +
                        "'updatedAt': '1970-01-01T00:00:00Z', " +
                        "'status': 'DONE', " +
                        "'author': {'id': '2', 'username': 'user2', 'email': 'email@email.com', 'roles': ['USER', 'MANAGER']}, " +
                        "'assignee': {'id': '2', 'username': 'user2', 'email': 'email@email.com', 'roles': ['USER', 'MANAGER']}, " +
                        "'observers': []}" +
                        "]}");
    }

    @Test
    @DisplayName("Test getAll() status 401")
    @WithAnonymousUser
    public void givenAnonymousUser_whenGetAll_thenErrorResponse() {
        webTestClient.get().uri("/api/v2/task")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': 'Authentication failure'}");
    }

    @Test
    @DisplayName("Test getAll() status 403")
    @WithMockUser(authorities = {"NO_ROLE"})
    public void givenInvalidRole_whenGetAll_thenErrorResponse() {
        webTestClient.get().uri("/api/v2/task")
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': 'No required authorities'}");
    }

    @Test
    @DisplayName("Test getById() status 200")
    @WithMockUser(authorities = {"USER"})
    public void givenIdAndRoleUser_whenGetById_thenTaskResponse() {
        String id = "1";

        webTestClient.get().uri("/api/v2/task/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'id': '1', " +
                        "'name': 'task1', " +
                        "'description': " +
                        "'description', " +
                        "'createdAt': '1970-01-01T00:00:00Z', " +
                        "'updatedAt': '1970-01-01T00:00:00Z', " +
                        "'status': 'TODO', " +
                        "'author': {'id': '1', 'username': 'user1', 'email': 'email@email.com', 'roles': ['USER']}, " +
                        "'assignee': {'id': '1', 'username': 'user1', 'email': 'email@email.com', 'roles': ['USER']}, " +
                        "'observers': [{'id': '1', 'username': 'user1', 'email': 'email@email.com', 'roles': ['USER']}]}");
    }

    @Test
    @DisplayName("Test getById() status 401")
    @WithAnonymousUser
    public void givenIdAndAnonymousUser_whenGetById_thenErrorResponse() {
        String id = "1";

        webTestClient.get().uri("/api/v2/task/{id}", id)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': 'Authentication failure'}");
    }

    @Test
    @DisplayName("Test getById() status 403")
    @WithMockUser(authorities = {"NO_ROLE"})
    public void givenIdAndInvalidRole_whenGetById_thenErrorResponse() {
        String id = "1";

        webTestClient.get().uri("/api/v2/task/{id}", id)
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': 'No required authorities'}");
    }

    @Test
    @DisplayName("Test getById() status 404")
    @WithMockUser(authorities = {"MANAGER"})
    public void givenNonexistentIdAndRoleManager_whenGetById_thenErrorResponse() {
        String id = "10";

        webTestClient.get().uri("/api/v2/task/{id}", id)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': \"Task with id '10' not found\"}");
    }

    @Test
    @DisplayName("Test create() status 201")
    @WithMockUser(authorities = {"MANAGER"})
    public void givenCreateTaskRequestAndRoleManager_whenCreate_thenVoid() {
        CreateTaskRequest request = createCreateTaskRequest();

        webTestClient.post().uri("/api/v2/task")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CreateTaskRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().isEmpty();

        assertEquals(3, taskRepository.findAll().count().block());
    }

    @Test
    @DisplayName("Test create() status 400")
    @WithMockUser(authorities = {"MANAGER"})
    public void givenInvalidCreateTaskRequestAndRoleManager_whenCreate_thenErrorResponse() {
        CreateTaskRequest request = createCreateTaskRequest();
        request.setStatus("NO_STATUS");

        webTestClient.post().uri("/api/v2/task")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CreateTaskRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': \"Task status must be any of ['TODO', 'IN_PROGRESS', 'DONE']\"}");

        assertEquals(2, taskRepository.findAll().count().block());
    }

    @Test
    @DisplayName("Test create() status 401")
    @WithAnonymousUser
    public void givenCreateTaskRequestAndAnonymousUser_whenCreate_thenErrorResponse() {
        CreateTaskRequest request = createCreateTaskRequest();

        webTestClient.post().uri("/api/v2/task")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CreateTaskRequest.class)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': 'Authentication failure'}");

        assertEquals(2, taskRepository.findAll().count().block());
    }

    @Test
    @DisplayName("Test create status 403")
    @WithMockUser(authorities = {"USER"})
    public void givenCreateTaskRequestAndRoleUser_whenCreate_thenErrorResponse() {
        CreateTaskRequest request = createCreateTaskRequest();

        webTestClient.post().uri("/api/v2/task")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CreateTaskRequest.class)
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': 'No required authorities'}");

        assertEquals(2, taskRepository.findAll().count().block());
    }

    @Test
    @DisplayName("Test create status 404")
    @WithMockUser(authorities = {"MANAGER"})
    public void givenNonexistentCreateTaskRequestUserIdAndRoleManager_whenCreate_thenErrorResponse() {
        CreateTaskRequest request = createCreateTaskRequest();
        request.setAuthorId("10");

        webTestClient.post().uri("/api/v2/task")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CreateTaskRequest.class)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': \"User with id '10' not found\"}");

        assertEquals(2, taskRepository.findAll().count().block());
    }

    @Test
    @DisplayName("Test updateById() status 204")
    @WithMockUser(authorities = {"MANAGER"})
    public void givenIdAndChangeTaskRequestAndRoleManager_whenUpdateById_thenVoid() {
        String id = "1";
        ChangeTaskRequest request = createChangeTaskRequest();

        webTestClient.put().uri("/api/v2/task/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), ChangeTaskRequest.class)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        assertEquals("changed_task", taskRepository.findById(id).map(Task::getName).block());
    }

    @Test
    @DisplayName("Test updateById() status 400")
    @WithMockUser(authorities = {"MANAGER"})
    public void givenIdAndInvalidChangeTaskRequestAndRoleManager_whenUpdateById_thenErrorResponse() {
        String id = "1";
        ChangeTaskRequest request = createChangeTaskRequest();
        request.setStatus("NO_STATUS");

        webTestClient.put().uri("/api/v2/task/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), ChangeTaskRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': \"Task status must be any of ['TODO', 'IN_PROGRESS', 'DONE']\"}");
    }

    @Test
    @DisplayName("Test updateById() status 401")
    @WithAnonymousUser
    public void givenIdAndCreateTaskRequestAndAnonymousUser_whenUpdateById_thenErrorResponse() {
        String id = "1";
        ChangeTaskRequest request = createChangeTaskRequest();

        webTestClient.put().uri("/api/v2/task/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), ChangeTaskRequest.class)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': 'Authentication failure'}");
    }

    @Test
    @DisplayName("Test updateById() status 403")
    @WithMockUser(authorities = {"USER"})
    public void givenIdAndCreateTaskRequestAndRoleUser_whenUpdateById_thenErrorResponse() {
        String id = "1";
        ChangeTaskRequest request = createChangeTaskRequest();

        webTestClient.put().uri("/api/v2/task/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), ChangeTaskRequest.class)
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': 'No required authorities'}");
    }

    @Test
    @DisplayName("Test updateById() status 404")
    @WithMockUser(authorities = {"MANAGER"})
    public void givenNonexistentIdAndCreateTaskRequestAndRoleManager_whenUpdateById_thenErrorResponse() {
        String id = "10";
        ChangeTaskRequest request = createChangeTaskRequest();

        webTestClient.put().uri("/api/v2/task/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), ChangeTaskRequest.class)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': \"Task with id '10' not found\"}");
    }

    @Test
    @DisplayName("Test addObserverById() status 200")
    @WithMockUser(authorities = {"USER"})
    public void givenTaskIdAndObserverIdAndRoleUser_whenAddObserverById_thenTaskResponse() {
        String taskId = "2";
        String observerId = "1";

        assertEquals(0, taskRepository.findById(taskId).map(task -> task.getObserverIds().size()).block());

        webTestClient.patch().uri("/api/v2/task/{id}/add-observer?observerId={observerId}", taskId, observerId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);

        assertEquals(1, taskRepository.findById(taskId).map(task -> task.getObserverIds().size()).block());
    }

    @Test
    @DisplayName("Test addObserverById() status 401")
    @WithAnonymousUser
    public void givenTaskIdAndObserverIdAndAnonymousUser_whenAddObserverById_thenErrorResponse() {
        String taskId = "2";
        String observerId = "1";

        webTestClient.patch().uri("/api/v2/task/{id}/add-observer?observerId={observerId}", taskId, observerId)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': 'Authentication failure'}");
    }

    @Test
    @DisplayName("Test addObserverById() status 403")
    @WithMockUser(authorities = {"NO_ROLE"})
    public void givenTaskIdAndObserverIdAndInvalidRole_whenAddObserverById_thenErrorResponse() {
        String taskId = "2";
        String observerId = "1";

        webTestClient.patch().uri("/api/v2/task/{id}/add-observer?observerId={observerId}", taskId, observerId)
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': 'No required authorities'}");
    }

    @Test
    @DisplayName("Test addObserverById() status 404")
    @WithMockUser(authorities = {"MANAGER"})
    public void givenNonexistentTaskIdAndObserverIdAndRoleManager_whenAddObserverById_thenErrorResponse() {
        String taskId = "10";
        String observerId = "1";

        webTestClient.patch().uri("/api/v2/task/{id}/add-observer?observerId={observerId}", taskId, observerId)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': \"Task with id '10' not found\"}");
    }

    @Test
    @DisplayName("Test removeObserverById() status 200")
    @WithMockUser(authorities = {"USER"})
    public void givenTaskIdAndObserverIdAndRoleUser_whenRemoveObserverById_thenTaskResponse() {
        String taskId = "1";
        String observerId = "1";

        assertEquals(1, taskRepository.findById(taskId).map(task -> task.getObserverIds().size()).block());

        webTestClient.patch().uri("/api/v2/task/{id}/remove-observer?observerId={observerId}", taskId, observerId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);

        assertEquals(0, taskRepository.findById(taskId).map(task -> task.getObserverIds().size()).block());
    }

    @Test
    @DisplayName("Test removeObserverById() status 401")
    @WithAnonymousUser
    public void givenTaskIdAndObserverIdAndAnonymousUser_whenRemoveObserverById_thenErrorResponse() {
        String taskId = "1";
        String observerId = "1";

        webTestClient.patch().uri("/api/v2/task/{id}/remove-observer?observerId={observerId}", taskId, observerId)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': 'Authentication failure'}");
    }

    @Test
    @DisplayName("Test removeObserverById() status 403")
    @WithMockUser(authorities = {"NO_ROLE"})
    public void givenTaskIdAndObserverIdAndInvalidRole_whenRemoveObserverById_thenErrorResponse() {
        String taskId = "1";
        String observerId = "1";

        webTestClient.patch().uri("/api/v2/task/{id}/remove-observer?observerId={observerId}", taskId, observerId)
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': 'No required authorities'}");
    }

    @Test
    @DisplayName("Test removeObserverById() status 404")
    @WithMockUser(authorities = {"MANAGER"})
    public void givenNonexistentTaskIdAndObserverIdAndRoleManager_whenRemoveObserverById_thenErrorResponse() {
        String taskId = "10";
        String observerId = "1";

        webTestClient.patch().uri("/api/v2/task/{id}/remove-observer?observerId={observerId}", taskId, observerId)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': \"Task with id '10' not found\"}");
    }

    @Test
    @DisplayName("Test deleteById() status 204")
    @WithMockUser(authorities = {"MANAGER"})
    public void givenIdAndRoleManager_whenDeleteById_thenVoid() {
        String id = "1";

        assertEquals(2, taskRepository.findAll().count().block());

        webTestClient.delete().uri("/api/v2/task/{id}", id)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        assertEquals(1, taskRepository.findAll().count().block());
    }

    @Test
    @DisplayName("Test deleteById() status 401")
    @WithAnonymousUser
    public void givenIdAndAnonymousUser_whenDeleteById_thenErrorResponse() {
        String id = "1";

        webTestClient.delete().uri("/api/v2/task/{id}", id)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': 'Authentication failure'}");
    }

    @Test
    @DisplayName("Test deleteById() status 403")
    @WithMockUser(authorities = {"USER"})
    public void givenIdAndRoleUser_whenDeleteById_thenErrorResponse() {
        String id = "1";

        webTestClient.delete().uri("/api/v2/task/{id}", id)
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': 'No required authorities'}");
    }

    private CreateTaskRequest createCreateTaskRequest() {
        return CreateTaskRequest.builder()
                .name("new_task")
                .description("description")
                .status("TODO")
                .authorId("1")
                .assigneeId("1")
                .observerIds(Collections.emptySet())
                .build();
    }

    private ChangeTaskRequest createChangeTaskRequest() {
        return ChangeTaskRequest.builder()
                .name("changed_task")
                .description("changed_description")
                .status("DONE")
                .assigneeId("2")
                .build();
    }
}
