package com.allitov.tasktracker.aop;

import com.allitov.tasktracker.error.ExceptionMessage;
import com.allitov.tasktracker.error.IllegalDataAccessException;
import com.allitov.tasktracker.model.entity.RoleType;
import com.allitov.tasktracker.security.AppUserDetails;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SecurityAspect {

    @Before(
            value = "execution(* com.allitov.tasktracker.web.controller.UserController.*ById(..)) " +
            "&& args(requestedUserId, userDetails, ..)",
            argNames = "requestedUserId,userDetails"
    )
    public void userControllerByIdMethodsAdvice(String requestedUserId, AppUserDetails userDetails) {
        if (userDetails.getUser().getRoles().contains(RoleType.MANAGER)) {
            return;
        }

        String userId = userDetails.getUser().getId();
        if (!userId.equals(requestedUserId)) {
            throw new IllegalDataAccessException(String.format(
                    ExceptionMessage.USER_DATA_ILLEGAL_ACCESS, userId, requestedUserId));
        }
    }
}
