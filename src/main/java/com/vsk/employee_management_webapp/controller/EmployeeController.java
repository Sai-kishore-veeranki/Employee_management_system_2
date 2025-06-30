package com.vsk.employee_management_webapp.controller;

import com.vsk.employee_management_webapp.dto.EmployeeRequest;
import com.vsk.employee_management_webapp.dto.EmployeeResponse;
import com.vsk.employee_management_webapp.dto.PaginatedResponse;
import com.vsk.employee_management_webapp.service.serviceInterface.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController // Changed to RestController
@RequestMapping("/api/employees") // RESTful endpoint
@Slf4j // For SLF4J logging
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
        log.info("EmployeeController initialized.");
    }

    @GetMapping // GET /api/employees?page=0&size=10&sort=firstName,asc
    public ResponseEntity<PaginatedResponse<EmployeeResponse>> getAllPaginatedEmployees(Pageable pageable) {
        log.info("Fetching paginated employees with Pageable: {}", pageable);
        Page<EmployeeResponse> employeePage = employeeService.findPaginated(pageable);
        PaginatedResponse<EmployeeResponse> response = new PaginatedResponse<>(
                employeePage.getContent(),
                employeePage.getNumber(),
                employeePage.getSize(),
                employeePage.getTotalElements(),
                employeePage.getTotalPages(),
                employeePage.isLast()
        );
        log.info("Returned paginated employees. Total elements: {}", employeePage.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all") // GET /api/employees/all (if you still need a non-paginated all list)
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        log.info("Fetching all employees (non-paginated).");
        List<EmployeeResponse> employees = employeeService.getAllEmployees();
        log.info("Returned {} total employees.", employees.size());
        return ResponseEntity.ok(employees);
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> addEmployee(@Valid @RequestBody EmployeeRequest employeeRequest) {
        log.info("Received request to add new employee: {}", employeeRequest.email());
        EmployeeResponse savedEmployee = employeeService.saveEmployee(employeeRequest);
        log.info("Employee added successfully with ID: {}", savedEmployee.id());
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    @GetMapping("/{id}") // GET /api/employees/{id}
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        log.info("Fetching employee by ID: {}", id);
        EmployeeResponse employee = employeeService.getEmployeeById(id);
        log.info("Employee found for ID: {}", id);
        return ResponseEntity.ok(employee);
    }

    @PutMapping("/{id}") // PUT /api/employees/{id}
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeRequest employeeRequest) {
        log.info("Received request to update employee with ID: {}", id);
        EmployeeResponse updatedEmployee = employeeService.updateEmployee(id, employeeRequest);
        log.info("Employee with ID {} updated successfully.", id);
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}") // DELETE /api/employees/{id}
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        log.info("Received request to delete employee with ID: {}", id);
        employeeService.deleteEmployeeById(id);
        log.info("Employee with ID {} deleted successfully.", id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}