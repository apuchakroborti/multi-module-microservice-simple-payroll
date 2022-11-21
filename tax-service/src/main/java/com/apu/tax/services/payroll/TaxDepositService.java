package com.apu.tax.services.payroll;


import com.apu.tax.dto.EmployeeTaxDepositDto;
import com.apu.tax.dto.request.TaxSearchCriteria;
import com.apu.tax.entity.payroll.EmployeeTaxDeposit;
import com.apu.tax.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;


public interface TaxDepositService {
    EmployeeTaxDepositDto insertIndividualTaxInfo(EmployeeTaxDepositDto employeeTaxDepositDto)throws GenericException;
    /*EmployeeTaxDeposit insertPayslipTaxInfo(MonthlyPaySlip monthlyPaySlip,
                                                             Employee employee,
                                                             Double taxToDepositForTheRequestMonth,
                                                             TaxType taxType,
                                                             LocalDate fromDate,
                                                             LocalDate toDate)throws GenericException;*/
    Page<EmployeeTaxDeposit> getAllTaxInfoByEmployeeId(Long employeeId, Pageable pageable) throws GenericException;
    Page<EmployeeTaxDeposit> getTaxInfoWithInDateRangeAndEmployeeId(TaxSearchCriteria criteria, Pageable pageable) throws GenericException;
}
