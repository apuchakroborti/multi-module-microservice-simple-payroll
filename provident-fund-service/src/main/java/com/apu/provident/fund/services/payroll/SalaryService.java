package com.apu.provident.fund.services.payroll;


import com.apu.provident.fund.dto.EmployeeSalaryDto;
import com.apu.provident.fund.dto.request.SalarySearchCriteria;
import com.apu.provident.fund.dto.response.ServiceResponse;
import com.apu.provident.fund.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface SalaryService {
    ServiceResponse<EmployeeSalaryDto> updateSalaryData(EmployeeSalaryDto employeeSalaryDto) throws GenericException;
    Page<EmployeeSalary> getSalaryDataWithInDateRangeAndEmployeeId(SalarySearchCriteria searchCriteria, Pageable pageable)throws GenericException;
    ServiceResponse<EmployeeSalaryDto> getCurrentSalaryByEmployeeId(Long employeeId)throws GenericException;
}
