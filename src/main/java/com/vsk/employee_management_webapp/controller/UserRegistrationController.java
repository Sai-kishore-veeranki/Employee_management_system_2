package com.vsk.employee_management_webapp.controller;

import com.vsk.employee_management_webapp.dto.UserRegistrationRequest;
import com.vsk.employee_management_webapp.model.User;
import com.vsk.employee_management_webapp.service.serviceInterface.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController // Changed to RestController
@RequestMapping("/api/register") // RESTful endpoint
@Slf4j // For SLF4J logging
public class UserRegistrationController {

    private final UserService userService;

    public UserRegistrationController(UserService userService) {
        this.userService = userService;
        log.info("UserRegistrationController initialized.");
    }

    @PostMapping // Handles POST requests to /api/register
    public ResponseEntity<String> registerUserAccount(@Valid @RequestBody UserRegistrationRequest registrationRequest) {
        log.info("Received user registration request for email: {}", registrationRequest.email());
        try {
            userService.save(registrationRequest);
            log.info("User registered successfully: {}", registrationRequest.email());
            return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error during user registration for email {}: {}", registrationRequest.email(), e.getMessage());
            return new ResponseEntity<>("Error registering user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}