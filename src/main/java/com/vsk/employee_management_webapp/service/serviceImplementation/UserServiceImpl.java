package com.vsk.employee_management_webapp.service.serviceImplementation;

import com.vsk.employee_management_webapp.dto.UserRegistrationRequest;
import com.vsk.employee_management_webapp.model.Role;
import com.vsk.employee_management_webapp.model.User;
import com.vsk.employee_management_webapp.repository.UserRepository;
import com.vsk.employee_management_webapp.service.serviceInterface.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final String DEFAULT_ROLE = "ROLE_USER";

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        log.debug("UserServiceImpl initialized.");
    }

    /**
     * Save a new user with default role assignment
     */
    @Override
    @Transactional
    public User save(UserRegistrationRequest registrationRequest) {
        log.debug("Registering new user: {}", registrationRequest.email());

        if (userRepository.findByEmail(registrationRequest.email()) != null) {
            log.warn("Registration failed: Email already in use - {}", registrationRequest.email());
            throw new IllegalArgumentException("Email already registered.");
        }

        User user = new User(
                registrationRequest.firstName(),
                registrationRequest.lastName(),
                registrationRequest.email(),
                passwordEncoder.encode(registrationRequest.password()),
                List.of(new Role(null, DEFAULT_ROLE))
        );

        User savedUser = userRepository.save(user);
        log.info("New user registered successfully: {}", savedUser.getEmail());
        return savedUser;
    }

    /**
     * Load user details by email for authentication
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Authenticating user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Authentication failed: No user found with email: {}", email);
                    return new UsernameNotFoundException("Invalid credentials.");
                });

        log.info("Authentication success: {}", email);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles())
        );
    }

    /**
     * Convert user roles to Spring Security authorities
     */
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
