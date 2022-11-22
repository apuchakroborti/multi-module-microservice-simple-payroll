package com.apu.provident.fund.services.payroll.impls;

import com.apu.provident.fund.dto.ProvidentFundDto;
import com.apu.provident.fund.services.payroll.ProvidentFundService;
import com.apu.provident.fund.dto.request.ProvidentFundSearchCriteria;
import com.apu.provident.fund.exceptions.GenericException;
import com.apu.provident.fund.entity.payroll.ProvidentFund;
import com.apu.provident.fund.repository.payroll.ProvidentFundRepository;
import com.apu.provident.fund.utils.Utils;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ProvidentFundServiceImpl implements ProvidentFundService {
//    Logger log = LoggerFactory.getLogger(ProvidentFundServiceImpl.class);

    @Autowired
    ProvidentFundRepository providentFundRepository;

    @Override
    public ProvidentFundDto savePfData(ProvidentFundDto providentFundDto) throws GenericException {
        log.info("ProvidentFundServiceImpl::savePfData start: saving pf data for the employee id: {}", providentFundDto.getEmployeeId());
        try{
            LocalDate start = providentFundDto.getMonth().with(firstDayOfMonth());
            LocalDate end = providentFundDto.getMonth().with(lastDayOfMonth());

            Optional<ProvidentFund> optionalProvidentFund = providentFundRepository.getByEmployeeIdAndFromDateAndToDate(providentFundDto.getEmployeeId(), start, end);
            if(optionalProvidentFund.isPresent()){
                Utils.copyProperty(optionalProvidentFund.get(), providentFundDto);
                return providentFundDto;
            }

            ProvidentFund providentFund = new ProvidentFund();
            providentFund.setFromDate(start);
            providentFund.setToDate(end);

            providentFund.setEmployeeContribution(providentFundDto.getGrossSalary()*0.60*7.5/100);
            providentFund.setCompanyContribution(providentFundDto.getGrossSalary()*0.60*7.5/100);

            providentFund.setComments("Monthly deposit to provident fund");

            providentFund.setEmployeeId(providentFundDto.getEmployeeId());

            providentFund.setCreatedBy(1L);
            providentFund.setCreateTime(LocalDateTime.now());

            providentFund = providentFundRepository.save(providentFund);

            Utils.copyProperty(providentFund, providentFundDto);
            log.info("ProvidentFundServiceImpl::savePfData end: save pf data done for the employee id: {}", providentFundDto.getEmployeeId());
            return providentFundDto;
        }catch (Exception e){
            log.error("ProvidentFundServiceImpl::savePfData error occurred while saving pf data for the employee id: {}", providentFundDto.getEmployeeId());
            throw new GenericException("Exception occurred while saving pf data!");
        }

    }
    @Override
    public Page<ProvidentFund> getPFInfoWithSearchCriteria(ProvidentFundSearchCriteria criteria, Pageable pageable) throws GenericException{
        Page<ProvidentFund> employeePFPage = providentFundRepository.getEmployeeMonthlyPFByDateRangeAndEmployeeId(
                criteria.getFromDate(), criteria.getToDate(), criteria.getEmployeeId(), pageable);

        return employeePFPage;
    }

    @Override
    public Optional<ProvidentFund> getByEmployeeIdAndFromDateAndToDate(Long employeeId, LocalDate fromDate, LocalDate toDate){
        return providentFundRepository.getByEmployeeIdAndFromDateAndToDate(employeeId, fromDate, toDate);
    }
}
