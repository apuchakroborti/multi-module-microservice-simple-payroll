package com.apu.provident.fund.services.payroll;

import com.apu.provident.fund.dto.request.ProvidentFundSearchCriteria;
import com.apu.provident.fund.exceptions.GenericException;
import com.apu.provident.fund.entity.payroll.ProvidentFund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;


public interface ProvidentFundService {
    ProvidentFund insertPfData(EmployeeSalary employeeSalary, LocalDate month) throws GenericException;
    Page<ProvidentFund> getPFInfoWithSearchCriteria(ProvidentFundSearchCriteria criteria, Pageable pageable) throws GenericException;
    Optional<ProvidentFund> getByEmployeeIdAndFromDateAndToDate(Long employeeId, LocalDate fromDate, LocalDate toDate);
}
