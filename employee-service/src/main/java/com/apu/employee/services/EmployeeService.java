package com.apu.employee.services;

import com.apu.employee.dto.EmployeeDto;
import com.apu.employee.dto.request.EmployeeSearchCriteria;
import com.apu.employee.entity.payroll.Employee;
import com.apu.employee.exceptions.GenericException;
import com.apu.employee.security_oauth2.models.security.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {
    EmployeeDto enrollEmployee(EmployeeDto employeeDto) throws GenericException;
    User addOauthUser(Employee employee, String password) throws GenericException;

    EmployeeDto findByUsername(String username) throws GenericException;
    EmployeeDto findEmployeeById(Long id) throws GenericException;
    EmployeeDto updateEmployeeById(Long id, EmployeeDto employeeBean) throws GenericException;
    Page<Employee> getEmployeeList(EmployeeSearchCriteria criteria, Pageable pageable) throws GenericException;
    Boolean  deleteEmployeeById(Long id) throws GenericException;
}
