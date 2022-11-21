//package com.apu.employee.services.payroll;
//
//
//import com.apu.employee.dto.EmployeeTaxDepositDto;
//import com.apu.employee.dto.request.TaxSearchCriteria;
//import com.apu.employee.dto.response.ServiceResponse;
//import com.apu.employee.exceptions.GenericException;
//import com.apu.employee.entity.payroll.Employee;
//import com.apu.employee.entity.payroll.EmployeeTaxDeposit;
//import com.apu.employee.entity.payroll.MonthlyPaySlip;
//import com.apu.employee.utils.TaxType;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//
//import java.time.LocalDate;
//
//
//public interface TaxDepositService {
//    EmployeeTaxDepositDto insertIndividualTaxInfo(EmployeeTaxDepositDto employeeTaxDepositDto)throws GenericException;
//    EmployeeTaxDeposit insertPayslipTaxInfo(MonthlyPaySlip monthlyPaySlip,
//                                            Employee employee,
//                                            Double taxToDepositForTheRequestMonth,
//                                            TaxType taxType,
//                                            LocalDate fromDate,
//                                            LocalDate toDate)throws GenericException;
//    Page<EmployeeTaxDeposit> getAllTaxInfoByEmployeeId(Long employeeId, Pageable pageable) throws GenericException;
//    Page<EmployeeTaxDeposit> getTaxInfoWithInDateRangeAndEmployeeId(TaxSearchCriteria criteria, Pageable pageable) throws GenericException;
//}
