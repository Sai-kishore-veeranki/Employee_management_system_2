package com.vsk.employee_management_webapp.service.serviceImplementation;

import com.vsk.employee_management_webapp.dto.UserRegistrationRequest;
import com.vsk.employee_management_webapp.model.Role;
import com.vsk.employee_management_webapp.model.User;
import com.vsk.employee_management_webapp.repository.UserRepository;
import com.vsk.employee_management_webapp.service.serviceInterface.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j // For SLF4J logging
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        log.info("UserServiceImpl initialized.");
    }

    @Override
    public User save(UserRegistrationRequest registrationRequest) {
        log.debug("Attempting to save new user with email: {}", registrationRequest.email());

        User user = new User(
                registrationRequest.firstName(),
                registrationRequest.lastName(),
                registrationRequest.email(),
                passwordEncoder.encode(registrationRequest.password()),
                List.of(new Role(null, "ROLE_USER")) // Use List.of for immutable list
        );
        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getEmail());
        return savedUser;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Attempting to load user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new UsernameNotFoundException("Invalid username or password.");
                });
        log.info("User found: {}", user.getEmail());
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())
        );
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles){
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }
}