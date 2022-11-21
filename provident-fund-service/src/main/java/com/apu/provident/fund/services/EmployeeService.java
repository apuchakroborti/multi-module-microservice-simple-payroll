package com.apu.provident.fund.services;

import com.apu.provident.fund.dto.request.EmployeeSearchCriteria;
import com.apu.provident.fund.exceptions.GenericException;
import com.apu.provident.fund.dto.EmployeeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {
    EmployeeDto enrollEmployee(EmployeeDto employeeDto) throws GenericException;

    EmployeeDto findByUsername(String username) throws GenericException;
    EmployeeDto findEmployeeById(Long id) throws GenericException;
    EmployeeDto updateEmployeeById(Long id, EmployeeDto employeeBean) throws GenericException;
    Page<Employee> getEmployeeList(EmployeeSearchCriteria criteria, Pageable pageable) throws GenericException;
    Boolean  deleteEmployeeById(Long id) throws GenericException;
}
