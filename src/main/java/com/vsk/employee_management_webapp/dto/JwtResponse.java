package com.vsk.employee_management_webapp.dto;

public record JwtResponse(
        String token,
        String type // e.g., "Bearer"
) {}
