package com.vsk.employee_management_webapp.service.serviceInterface;

import com.vsk.employee_management_webapp.dto.EmployeeRequest;
import com.vsk.employee_management_webapp.dto.EmployeeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {
    List<EmployeeResponse> getAllEmployees();
    EmployeeResponse saveEmployee(EmployeeRequest employeeRequest);
    EmployeeResponse getEmployeeById(long id);
    void deleteEmployeeById(long id);
    Page<EmployeeResponse> findPaginated(Pageable pageable); // Modern pagination
    EmployeeResponse updateEmployee(long id, EmployeeRequest employeeRequest); // Added update method
}