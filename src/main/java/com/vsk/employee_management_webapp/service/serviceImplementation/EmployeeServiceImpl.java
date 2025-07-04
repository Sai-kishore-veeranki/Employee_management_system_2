package com.vsk.employee_management_webapp.service.serviceImplementation;

import com.vsk.employee_management_webapp.dto.EmployeeRequest;
import com.vsk.employee_management_webapp.dto.EmployeeResponse;
import com.vsk.employee_management_webapp.exception.ResourceNotFoundException;
import com.vsk.employee_management_webapp.model.Employee;
import com.vsk.employee_management_webapp.repository.EmployeeRepository;
import com.vsk.employee_management_webapp.service.serviceInterface.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
        log.debug("EmployeeServiceImpl initialized.");
    }

    /**
     * Fetch all employees in non-paginated format
     */
    @Override
    public List<EmployeeResponse> getAllEmployees() {
        log.info("Fetching all employee records.");
        return employeeRepository.findAll()
                .stream()
                .map(this::mapToEmployeeResponse)
                .collect(Collectors.toList());
    }

    /**
     * Save a new employee to the database
     */
    @Override
    @Transactional
    public EmployeeResponse saveEmployee(EmployeeRequest request) {
        log.debug("Saving employee: {}", request.email());
        Employee employee = new Employee(request.firstName(), request.lastName(), request.email());
        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Saved employee with ID {}", savedEmployee.getId());
        return mapToEmployeeResponse(savedEmployee);
    }

    /**
     * Fetch an employee by ID
     */
    @Override
    public EmployeeResponse getEmployeeById(long id) {
        log.debug("Fetching employee by ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        log.info("Found employee: {}", employee.getEmail());
        return mapToEmployeeResponse(employee);
    }

    /**
     * Delete employee by ID
     */
    @Override
    @Transactional
    public void deleteEmployeeById(long id) {
        log.debug("Deleting employee with ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        employeeRepository.delete(employee);
        log.info("Deleted employee: {}", employee.getEmail());
    }

    /**
     * Return paginated employee data
     */
    @Override
    public Page<EmployeeResponse> getPaginatedEmployees(Pageable pageable) {
        log.debug("Fetching paginated employees - Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Employee> page = employeeRepository.findAll(pageable);
        List<EmployeeResponse> responses = page.stream()
                .map(this::mapToEmployeeResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, page.getTotalElements());
    }

    /**
     * Update employee record
     */
    @Override
    @Transactional
    public EmployeeResponse updateEmployee(long id, EmployeeRequest request) {
        log.debug("Updating employee with ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        employee.setFirstName(request.firstName());
        employee.setLastName(request.lastName());
        employee.setEmail(request.email());

        Employee updated = employeeRepository.save(employee);
        log.info("Updated employee: {} (ID: {})", updated.getEmail(), updated.getId());
        return mapToEmployeeResponse(updated);
    }

    /**
     * Map entity to DTO
     */
    private EmployeeResponse mapToEmployeeResponse(Employee employee) {
        return new EmployeeResponse(employee.getId(), employee.getFirstName(), employee.getLastName(), employee.getEmail());
    }
}
