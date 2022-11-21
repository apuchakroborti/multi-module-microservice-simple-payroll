package com.apu.provident.fund.services.payroll;


import com.apu.provident.fund.dto.EmployeeTaxDepositDto;
import com.apu.provident.fund.dto.request.TaxSearchCriteria;
import com.apu.provident.fund.dto.response.ServiceResponse;
import com.apu.provident.fund.exceptions.GenericException;
import com.apu.provident.fund.utils.TaxType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;


public interface TaxDepositService {
    EmployeeTaxDepositDto insertIndividualTaxInfo(EmployeeTaxDepositDto employeeTaxDepositDto)throws GenericException;
    ServiceResponse<EmployeeTaxDeposit> insertPayslipTaxInfo(MonthlyPaySlip monthlyPaySlip,
                                            Employee employee,
                                            Double taxToDepositForTheRequestMonth,
                                            TaxType taxType,
                                            LocalDate fromDate,
                                            LocalDate toDate)throws GenericException;
    Page<EmployeeTaxDeposit> getAllTaxInfoByEmployeeId(Long employeeId, Pageable pageable) throws GenericException;
    Page<EmployeeTaxDeposit> getTaxInfoWithInDateRangeAndEmployeeId(TaxSearchCriteria criteria, Pageable pageable) throws GenericException;
}
