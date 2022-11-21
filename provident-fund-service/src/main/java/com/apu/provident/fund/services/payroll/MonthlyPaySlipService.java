package com.apu.provident.fund.services.payroll;


import com.apu.provident.fund.dto.MonthlyPaySlipDto;
import com.apu.provident.fund.dto.request.MonthlyPaySlipRequestDto;
import com.apu.provident.fund.dto.request.PayslipSearchCriteria;
import com.apu.provident.fund.dto.response.ServiceResponse;
import com.apu.provident.fund.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface MonthlyPaySlipService {
   ServiceResponse<MonthlyPaySlipDto> generatePaySlip(MonthlyPaySlipRequestDto monthlyPaySlipRequestDto) throws GenericException;
   Page<MonthlyPaySlip> getPaySlipWithInDateRangeAndEmployeeId(PayslipSearchCriteria criteria, Pageable pageable) throws GenericException;
   void generatePayslipForCurrentFinancialYear(Employee employee, EmployeeSalary employeeSalary, LocalDate fromDate) throws GenericException;
}
