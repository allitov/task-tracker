package com.allitov.tasktracker.model.repository;

import com.allitov.tasktracker.model.entity.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
}
