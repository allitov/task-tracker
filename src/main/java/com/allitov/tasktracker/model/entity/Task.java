package com.allitov.tasktracker.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tasks")
public class Task {

    @Id
    private String id;

    private String name;

    private String description;

    private Instant createdAt;

    private Instant updatedAt;

    private TaskStatus status;

    private String authorId;

    private String assigneeId;

    @Builder.Default
    private Set<String> observerIds = new HashSet<>();

    @ReadOnlyProperty
    private User author;

    @ReadOnlyProperty
    private User assignee;

    @ReadOnlyProperty
    @Builder.Default
    private Set<User> observers = new HashSet<>();

    public void addObserverId(String id) {
        observerIds.add(id);
    }

    public void removeObserverId(String id) {
        observerIds.remove(id);
    }

    public enum TaskStatus {
        TODO,
        IN_PROGRESS,
        DONE
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description) &&
                status == task.status && Objects.equals(assigneeId, task.assigneeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, assigneeId);
    }
}
