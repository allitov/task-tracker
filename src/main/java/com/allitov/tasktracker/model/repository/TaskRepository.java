package com.allitov.tasktracker.model.repository;

import com.allitov.tasktracker.model.entity.Task;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TaskRepository extends ReactiveMongoRepository<Task, String> {
}
