package com.vsk.employee_management_webapp.controller;

import com.vsk.employee_management_webapp.dto.EmployeeRequest;
import com.vsk.employee_management_webapp.dto.EmployeeResponse;
import com.vsk.employee_management_webapp.dto.PaginatedResponse;
import com.vsk.employee_management_webapp.service.serviceInterface.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Employees", description = "Manage employee records")
@RestController
@RequestMapping("/api/employees")
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
        log.debug("EmployeeController initialized.");
    }

    @Operation(summary = "Get paginated employees", description = "Returns employee data page-wise")
    @GetMapping
    public ResponseEntity<PaginatedResponse<EmployeeResponse>> getAllPaginatedEmployees(Pageable pageable) {
        log.debug("Fetching employees with Pageable: {}", pageable);
        Page<EmployeeResponse> page = employeeService.getPaginatedEmployees(pageable);
        PaginatedResponse<EmployeeResponse> response = new PaginatedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
        log.info("Returned {} employees", page.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all employees", description = "Returns all employees (non-paginated)")
    @GetMapping("/all")
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        log.debug("Fetching all employees");
        List<EmployeeResponse> employees = employeeService.getAllEmployees();
        log.info("Returned {} employees", employees.size());
        return ResponseEntity.ok(employees);
    }

    @Operation(summary = "Add new employee", description = "Create a new employee record")
    @PostMapping
    public ResponseEntity<EmployeeResponse> addEmployee(@Valid @RequestBody EmployeeRequest request) {
        log.debug("Adding new employee: {}", request.email());
        EmployeeResponse saved = employeeService.saveEmployee(request);
        log.info("Employee created with ID: {}", saved.id());
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @Operation(summary = "Get employee by ID", description = "Fetch employee details using ID")
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        log.debug("Fetching employee by ID: {}", id);
        EmployeeResponse employee = employeeService.getEmployeeById(id);
        log.info("Employee found for ID: {}", id);
        return ResponseEntity.ok(employee);
    }

    @Operation(summary = "Update employee", description = "Modify an existing employee record")
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        log.debug("Updating employee with ID: {}", id);
        EmployeeResponse updated = employeeService.updateEmployee(id, request);
        log.info("Employee updated with ID: {}", id);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete employee", description = "Remove an employee record by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        log.debug("Deleting employee with ID: {}", id);
        employeeService.deleteEmployeeById(id);
        log.info("Employee deleted with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
