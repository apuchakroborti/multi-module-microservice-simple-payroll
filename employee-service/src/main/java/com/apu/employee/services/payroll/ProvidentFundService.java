//package com.apu.employee.services.payroll;
//
//import com.apu.employee.dto.request.ProvidentFundSearchCriteria;
//import com.apu.employee.entity.payroll.EmployeeSalary;
//import com.apu.employee.exceptions.GenericException;
//import com.apu.employee.entity.payroll.ProvidentFund;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//
//import java.time.LocalDate;
//import java.util.Optional;
//
//
//public interface ProvidentFundService {
//    ProvidentFund insertPfData(EmployeeSalary employeeSalary, LocalDate month) throws GenericException;
//    Page<ProvidentFund> getPFInfoWithSearchCriteria(ProvidentFundSearchCriteria criteria, Pageable pageable) throws GenericException;
//    Optional<ProvidentFund> getByEmployeeIdAndFromDateAndToDate(Long employeeId, LocalDate fromDate, LocalDate toDate);
//}
