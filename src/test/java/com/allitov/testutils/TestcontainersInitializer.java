package com.allitov.testutils;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.MongoDBContainer;

public class TestcontainersInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final MongoDBContainer MONGODB = new MongoDBContainer("mongo:6.0.13");

    static {
        MONGODB.withReuse(true);
        MONGODB.start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        TestPropertyValues.of(
                "spring.data.mongodb.uri=" + MONGODB.getReplicaSetUrl()
        ).applyTo(applicationContext.getEnvironment());
    }
}
