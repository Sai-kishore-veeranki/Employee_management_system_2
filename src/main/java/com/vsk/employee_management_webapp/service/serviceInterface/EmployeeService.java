package com.vsk.employee_management_webapp.service.serviceInterface;

import com.vsk.employee_management_webapp.dto.EmployeeRequest;
import com.vsk.employee_management_webapp.dto.EmployeeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {

    /**
     * Retrieve all employee records
     */
    List<EmployeeResponse> getAllEmployees();

    /**
     * Persist a new employee
     */
    EmployeeResponse saveEmployee(EmployeeRequest employeeRequest);

    /**
     * Retrieve a specific employee by ID
     */
    EmployeeResponse getEmployeeById(long id);

    /**
     * Remove an employee by ID
     */
    void deleteEmployeeById(long id);

    /**
     * Retrieve paginated employees for UI or API consumption
     */
    Page<EmployeeResponse> getPaginatedEmployees(Pageable pageable); // Renamed for semantic clarity

    /**
     * Update an existing employee's details
     */
    EmployeeResponse updateEmployee(long id, EmployeeRequest employeeRequest);
}
