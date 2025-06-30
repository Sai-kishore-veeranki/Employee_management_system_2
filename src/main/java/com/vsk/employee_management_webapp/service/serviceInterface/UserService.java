package com.vsk.employee_management_webapp.service.serviceInterface;

import com.vsk.employee_management_webapp.dto.UserRegistrationRequest;
import com.vsk.employee_management_webapp.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User save(UserRegistrationRequest registrationRequest);
}