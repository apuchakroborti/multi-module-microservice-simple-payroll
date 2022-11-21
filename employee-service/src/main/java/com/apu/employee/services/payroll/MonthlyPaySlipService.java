//package com.apu.employee.services.payroll;
//
//
//import com.apu.employee.dto.MonthlyPaySlipDto;
//import com.apu.employee.dto.request.MonthlyPaySlipRequestDto;
//import com.apu.employee.dto.request.PayslipSearchCriteria;
//import com.apu.employee.entity.payroll.Employee;
//import com.apu.employee.entity.payroll.EmployeeSalary;
//import com.apu.employee.entity.payroll.MonthlyPaySlip;
//import com.apu.employee.exceptions.GenericException;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//
//import java.time.LocalDate;
//
//public interface MonthlyPaySlipService {
//   MonthlyPaySlipDto generatePaySlip(MonthlyPaySlipRequestDto monthlyPaySlipRequestDto) throws GenericException;
//   Page<MonthlyPaySlip> getPaySlipWithInDateRangeAndEmployeeId(PayslipSearchCriteria criteria, Pageable pageable) throws GenericException;
//   void generatePayslipForCurrentFinancialYear(Employee employee, EmployeeSalary employeeSalary, LocalDate fromDate) throws GenericException;
//}
