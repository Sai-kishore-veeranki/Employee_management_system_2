package com.vsk.employee_management_webapp.service.serviceInterface;

import com.vsk.employee_management_webapp.dto.UserRegistrationRequest;
import com.vsk.employee_management_webapp.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Service interface for user management and authentication.
 * Extends Spring Security's UserDetailsService for integration with authentication.
 */
public interface UserService extends UserDetailsService {

    /**
     * Registers a new user with default role and encodes the password.
     * Throws IllegalArgumentException if the email is already in use.
     *
     * @param registrationRequest the incoming registration data
     * @return the saved user entity
     */
    User save(UserRegistrationRequest registrationRequest);
}
