package com.allitov.tasktracker.integration;

import com.allitov.tasktracker.model.entity.RoleType;
import com.allitov.tasktracker.model.entity.User;
import com.allitov.tasktracker.model.repository.UserRepository;
import com.allitov.tasktracker.web.dto.request.UserRequest;
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

import java.util.List;
import java.util.Set;

@SpringBootTest
@EnableTestcontainers
@AutoConfigureWebTestClient
public class UserControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

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
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll().block();
    }

    @Test
    @DisplayName("Test getAll() status 200")
    @WithMockUser(authorities = {"USER"})
    public void givenRoleUser_whenGetAll_thenUserListResponse() {
        webTestClient.get().uri("/api/v2/user")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'users': [" +
                        "{'id': '1', " +
                        "'username': 'user1', " +
                        "'email': 'email@email.com', " +
                        "'roles': ['USER']}, " +
                        "{'id': '2', " +
                        "'username': 'user2', " +
                        "'email': 'email@email.com', " +
                        "'roles': ['USER', 'MANAGER']}" +
                        "]}");
    }

    @Test
    @DisplayName("Test getAll() status 401")
    @WithAnonymousUser
    public void givenAnonymousUser_whenGetAll_thenErrorResponse() {
        webTestClient.get().uri("/api/v2/user")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': 'Authentication failure'}");
    }

    @Test
    @DisplayName("Test getAll() status 403")
    @WithMockUser(authorities = {"NOT_USER"})
    public void givenInvalidAuthority_whenGetAll_thenErrorResponse() {
        webTestClient.get().uri("/api/v2/user")
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': 'No required authorities'}");
    }

    @Test
    @DisplayName("Test getById() status 200")
    @WithMockUser(authorities = {"USER"})
    public void givenIdAndRoleUser_whenGetById_thenUserResponse() {
        String id = "1";

        webTestClient.get().uri("/api/v2/user/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'id': '1', 'username': 'user1', 'email': 'email@email.com', 'roles': ['USER']}");
    }

    @Test
    @DisplayName("Test getById() status 401")
    @WithAnonymousUser
    public void givenIdAndAnonymousUser_whenGetById_thenErrorResponse() {
        String id = "1";

        webTestClient.get().uri("/api/v2/user/{id}", id)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': 'Authentication failure'}");
    }

    @Test
    @DisplayName("Test getById() status 403")
    @WithMockUser(authorities = {"NOT_USER"})
    public void givenIdAndInvalidRole_whenGetById_thenErrorResponse() {
        String id = "1";

        webTestClient.get().uri("/api/v2/user/{id}", id)
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

        webTestClient.get().uri("/api/v2/user/{id}", id)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': \"User with id '10' not found\"}");
    }

    @Test
    @DisplayName("Test create() status 201")
    @WithAnonymousUser
    public void givenUserRequest_whenCreate_thenVoid() {
        UserRequest request = createUserRequest();

        webTestClient.post().uri("/api/v2/user")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), UserRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("/api/v2/user/" +
                        userRepository.findByUsername("new_user").map(User::getId).block())
                .expectBody().isEmpty();
    }

    @Test
    @DisplayName("Test create() status 400")
    @WithAnonymousUser
    public void givenInvalidUserRequest_whenCreate_thenErrorResponse() {
        UserRequest request = createUserRequest();
        request.setRoles(Set.of("NOT_ROLE"));

        webTestClient.post().uri("/api/v2/user")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), UserRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("{'errorMessage': \"Invalid roles. Must be any of ['USER', 'MANAGER']\"}");
    }

    private UserRequest createUserRequest() {
        return UserRequest.builder()
                .username("new_user")
                .email("new@email.com")
                .password("123")
                .roles(Set.of(RoleType.USER.name()))
                .build();
    }
}
