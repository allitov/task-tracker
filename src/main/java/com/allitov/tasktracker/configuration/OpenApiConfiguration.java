package com.allitov.tasktracker.configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@SecurityScheme(
        type = SecuritySchemeType.HTTP,
        name = "Basic authorization",
        scheme = "basic"
)
public class OpenApiConfiguration {

    @Bean
    public OpenAPI openApiDescription() {
        Server localhostServer = new Server()
                .url("http://localhost:8080")
                .description("Local env");

        Info info = new Info()
                .title("Task Tracker API")
                .version("2.0")
                .description("API for task tracker services");

        return new OpenAPI().info(info).servers(List.of(localhostServer));
    }
}
