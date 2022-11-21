package com.apu.provident.fund.services.payroll.impls;

import com.apu.provident.fund.services.payroll.ProvidentFundService;
import com.apu.provident.fund.dto.request.ProvidentFundSearchCriteria;
import com.apu.provident.fund.exceptions.GenericException;
import com.apu.provident.fund.entity.payroll.ProvidentFund;
import com.apu.provident.fund.repository.payroll.ProvidentFundRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

@Service
public class ProvidentFundServiceImpl implements ProvidentFundService {
    Logger logger = LoggerFactory.getLogger(ProvidentFundServiceImpl.class);

    @Autowired
    ProvidentFundRepository employeeProvidentFundRepository;

    @Override
    public ProvidentFund insertPfData(EmployeeSalary employeeSalary, LocalDate month) throws GenericException {
        ProvidentFund providentFund = new ProvidentFund();

        LocalDate start = month.with(firstDayOfMonth());
        LocalDate end = month.with(lastDayOfMonth());

        providentFund.setFromDate(start);
        providentFund.setToDate(end);

        providentFund.setEmployeeContribution(employeeSalary.getBasicSalary()*7.5/100);
        providentFund.setCompanyContribution(employeeSalary.getBasicSalary()*7.5/100);

        providentFund.setComments("Monthly deposit to provident fund");

        providentFund.setEmployee(employeeSalary.getEmployee());

        providentFund.setCreatedBy(1L);
        providentFund.setCreateTime(LocalDateTime.now());

        providentFund = employeeProvidentFundRepository.save(providentFund);

        return providentFund;
    }
    @Override
    public Page<ProvidentFund> getPFInfoWithSearchCriteria(ProvidentFundSearchCriteria criteria, Pageable pageable) throws GenericException{
        Page<ProvidentFund> employeePFPage = employeeProvidentFundRepository.getEmployeeMonthlyPFByDateRangeAndEmployeeId(
                criteria.getFromDate(), criteria.getToDate(), criteria.getEmployeeId(), pageable);

        return employeePFPage;
    }

    @Override
    public Optional<ProvidentFund> getByEmployeeIdAndFromDateAndToDate(Long employeeId, LocalDate fromDate, LocalDate toDate){
        return employeeProvidentFundRepository.getByEmployeeIdAndFromDateAndToDate(employeeId, fromDate, toDate);
    }
}
