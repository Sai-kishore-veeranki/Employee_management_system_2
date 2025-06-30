package com.vsk.employee_management_webapp.dto;

import java.util.List;

public record PaginatedResponse<T>(
        List<T> content,
        int pageNo,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean last
) {}
