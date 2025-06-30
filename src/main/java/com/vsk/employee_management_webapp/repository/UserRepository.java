package com.vsk.employee_management_webapp.repository;

import com.vsk.employee_management_webapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Changed to Optional for better practice

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // Use Optional for clarity
}