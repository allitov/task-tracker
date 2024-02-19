package com.allitov.tasktracker.web.mapper;

import com.allitov.tasktracker.model.entity.User;
import com.allitov.tasktracker.web.dto.request.UserRequest;
import com.allitov.tasktracker.web.dto.response.UserListResponse;
import com.allitov.tasktracker.web.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User requestToUser(UserRequest request);

    default User requestToUser(String id, UserRequest request) {
        User user = requestToUser(request);
        user.setId(id);

        return user;
    }

    UserResponse userToResponse(User user);

    List<UserResponse> userIterableToResponseList(Iterable<User> users);

    default UserListResponse userListToUserListResponse(List<User> users) {
        UserListResponse response = new UserListResponse();
        response.setUsers(userIterableToResponseList(users));

        return response;
    }
}
