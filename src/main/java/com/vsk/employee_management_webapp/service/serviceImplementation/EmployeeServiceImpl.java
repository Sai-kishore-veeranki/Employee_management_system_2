package com.vsk.employee_management_webapp.service.serviceImplementation;

import com.vsk.employee_management_webapp.dto.EmployeeRequest;
import com.vsk.employee_management_webapp.dto.EmployeeResponse;
import com.vsk.employee_management_webapp.model.Employee;
import com.vsk.employee_management_webapp.repository.EmployeeRepository;
import com.vsk.employee_management_webapp.service.serviceInterface.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j // For SLF4J logging
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
        log.info("EmployeeServiceImpl initialized.");
    }

    @Override
    public List<EmployeeResponse> getAllEmployees() {
        log.info("Fetching all employees.");
        return employeeRepository.findAll().stream()
                .map(this::mapToEmployeeResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeResponse saveEmployee(EmployeeRequest employeeRequest) {
        log.debug("Attempting to save employee: {}", employeeRequest.email());
        Employee employee = new Employee(
                employeeRequest.firstName(),
                employeeRequest.lastName(),
                employeeRequest.email()
        );
        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Employee saved successfully with ID: {}", savedEmployee.getId());
        return mapToEmployeeResponse(savedEmployee);
    }

    @Override
    public EmployeeResponse getEmployeeById(long id) {
        log.debug("Fetching employee by ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Employee not found for ID: {}", id);
                    return new RuntimeException("Employee not found for id :: " + id);
                });
        log.info("Employee found with ID: {}", id);
        return mapToEmployeeResponse(employee);
    }

    @Override
    public void deleteEmployeeById(long id) {
        log.debug("Attempting to delete employee by ID: {}", id);
        if (!employeeRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent employee with ID: {}", id);
            throw new RuntimeException("Employee not found for id :: " + id);
        }
        this.employeeRepository.deleteById(id);
        log.info("Employee deleted successfully with ID: {}", id);
    }

    @Override
    public Page<EmployeeResponse> findPaginated(Pageable pageable) {
        log.debug("Fetching paginated employees - Page: {}, Size: {}, Sort: {}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<Employee> employeePage = this.employeeRepository.findAll(pageable);
        List<EmployeeResponse> employeeResponses = employeePage.getContent().stream()
                .map(this::mapToEmployeeResponse)
                .collect(Collectors.toList());
        log.info("Retrieved {} employees for page {} of {}", employeeResponses.size(),
                employeePage.getNumber() + 1, employeePage.getTotalPages());
        return new PageImpl<>(employeeResponses, pageable, employeePage.getTotalElements());
    }

    @Override
    public EmployeeResponse updateEmployee(long id, EmployeeRequest employeeRequest) {
        log.debug("Attempting to update employee with ID: {}", id);
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Employee not found for update with ID: {}", id);
                    return new RuntimeException("Employee not found for id :: " + id);
                });

        existingEmployee.setFirstName(employeeRequest.firstName());
        existingEmployee.setLastName(employeeRequest.lastName());
        existingEmployee.setEmail(employeeRequest.email());

        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        log.info("Employee updated successfully with ID: {}", updatedEmployee.getId());
        return mapToEmployeeResponse(updatedEmployee);
    }

    private EmployeeResponse mapToEmployeeResponse(Employee employee) {
        return new EmployeeResponse(employee.getId(), employee.getFirstName(), employee.getLastName(), employee.getEmail());
    }
}