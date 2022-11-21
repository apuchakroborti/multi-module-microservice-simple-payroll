package com.apu.payslip.services.payroll;


import com.apu.payslip.dto.MonthlyPaySlipDto;
import com.apu.payslip.dto.request.MonthlyPaySlipRequestDto;
import com.apu.payslip.dto.request.PayslipSearchCriteria;
import com.apu.payslip.entity.payroll.MonthlyPaySlip;
import com.apu.payslip.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface MonthlyPaySlipService {
   MonthlyPaySlipDto generatePaySlip(MonthlyPaySlipRequestDto monthlyPaySlipRequestDto) throws GenericException;
   Page<MonthlyPaySlip> getPaySlipWithInDateRangeAndEmployeeId(PayslipSearchCriteria criteria, Pageable pageable) throws GenericException;
   void generatePayslipForCurrentFinancialYear(Long employeeId, Double employeeSalary, LocalDate fromDate) throws GenericException;
}
