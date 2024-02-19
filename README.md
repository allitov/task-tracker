# Task Tracker
**API for task tracker services.**

The application provides opportunity to create tasks and add users to them.

## Running locally
### Clone the repository and move to the working directory
```shell
git clone https://github.com/allitov/task-tracker.git
cd task-tracker
```

### Run the application
```shell
docker-compose --file ./docker/docker-compose.yaml up -d
```

#### Stop the application
```shell
docker-compose --project-name="task-tracker" down
```

### Run application environment only
```shell
docker-compose --file ./docker/docker-compose-env.yaml up -d
```

#### Stop application environment
```shell
docker-compose --project-name="task-tracker-env" down
```

## Documentation
To familiarize yourself with the application's API and see example queries, 
you can refer to the [interactive Swagger documentation](http://localhost:8080/webjars/swagger-ui/index.html) 
(available only after launching the application).