package com.apu.employee.services.payroll;


import com.apu.employee.dto.EmployeeSalaryDto;
import com.apu.employee.dto.request.SalarySearchCriteria;
import com.apu.employee.exceptions.GenericException;
import com.apu.employee.entity.payroll.EmployeeSalary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface SalaryService {
    EmployeeSalaryDto updateSalaryData(EmployeeSalaryDto employeeSalaryDto) throws GenericException;
    Page<EmployeeSalary> getSalaryDataWithInDateRangeAndEmployeeId(SalarySearchCriteria searchCriteria, Pageable pageable)throws GenericException;
    EmployeeSalaryDto getCurrentSalaryByEmployeeId(Long employeeId)throws GenericException;
}
