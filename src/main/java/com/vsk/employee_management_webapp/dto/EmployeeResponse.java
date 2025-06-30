package com.vsk.employee_management_webapp.dto;

public record EmployeeResponse(
        Long id,
        String firstName,
        String lastName,
        String email
) {}
