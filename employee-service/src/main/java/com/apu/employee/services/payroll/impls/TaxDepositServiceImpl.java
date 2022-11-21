//package com.apu.employee.services.payroll.impls;
//
//import com.apu.employee.dto.EmployeeTaxDepositDto;
//import com.apu.employee.dto.request.TaxSearchCriteria;
//import com.apu.employee.exceptions.GenericException;
//import com.apu.employee.entity.payroll.Employee;
//import com.apu.employee.entity.payroll.EmployeeTaxDeposit;
//import com.apu.employee.entity.payroll.MonthlyPaySlip;
//import com.apu.employee.repository.payroll.EmployeeRepository;
//import com.apu.employee.repository.payroll.TaxDepositRepository;
//import com.apu.employee.services.payroll.TaxDepositService;
//import com.apu.employee.utils.Defs;
//import com.apu.employee.utils.TaxType;
//import com.apu.employee.utils.Utils;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//@Service
//@Slf4j
//public class TaxDepositServiceImpl implements TaxDepositService {
//
//    private final TaxDepositRepository taxDepositRepository;
//    private final EmployeeRepository employeeRepository;
//
//    @Autowired
//    TaxDepositServiceImpl(TaxDepositRepository taxDepositRepository,
//                          EmployeeRepository employeeRepository){
//        this.taxDepositRepository = taxDepositRepository;
//        this.employeeRepository = employeeRepository;
//    }
//
//
//    //An employee can save his/her tax info only
//    @Override
//    public EmployeeTaxDepositDto insertIndividualTaxInfo(EmployeeTaxDepositDto employeeTaxDepositDto) throws GenericException{
//        try {
//            log.info("TaxDepositServiceImpl::insertIndividualTaxInfo service start: employee Id: {}",employeeTaxDepositDto.getEmployee().getId());
//            Optional<Employee> employee = employeeRepository.getLoggedInEmployee();
//
//            Optional<Employee> optionalEmployee = employeeRepository.findById(
//                    employee.isPresent() ? employee.get().getId(): employeeTaxDepositDto.getEmployee().getId()
//            );
//            if (!optionalEmployee.isPresent()){
//                log.debug("TaxDepositServiceImpl::insertIndividualTaxInfo: employee not found by Id: {}",employeeTaxDepositDto.getEmployee().getId());
//                throw new GenericException(Defs.EMPLOYEE_NOT_FOUND);
//            }
//
//            EmployeeTaxDeposit employeeTaxDeposit = new EmployeeTaxDeposit();
//            Utils.copyProperty(employeeTaxDepositDto, employeeTaxDeposit);
//
//            employeeTaxDeposit.setEmployee(optionalEmployee.get());
//            employeeTaxDeposit.setCreatedBy(1L);
//            employeeTaxDeposit.setCreateTime(LocalDateTime.now());
//
//            employeeTaxDeposit = taxDepositRepository.save(employeeTaxDeposit);
//
//            log.debug("TaxDepositServiceImpl::insertIndividualTaxInfo: employeeTaxDeposity info: {}", Utils.jsonAsString(employeeTaxDeposit));
//
//            Utils.copyProperty(employeeTaxDeposit, employeeTaxDepositDto);
//            log.info("TaxDepositServiceImpl::insertIndividualTaxInfo service end: employee Id: {}",employeeTaxDepositDto.getEmployee().getId());
//
//            return employeeTaxDepositDto;
//        }catch (Exception e){
//            log.error("Error occurred while inserting individual tax info, employeeId: {}", employeeTaxDepositDto.getEmployee().getId());
//            throw new GenericException("Internal server error", e);
//        }
//    }
//
//    //An employee can save his/her tax info only
//    @Override
//    public ServiceResponse<EmployeeTaxDeposit> insertPayslipTaxInfo(MonthlyPaySlip monthlyPaySlip, Employee employee,
//                                                   Double taxToDepositForTheRequestMonth, TaxType taxType,
//                                                   LocalDate fromDate, LocalDate toDate)throws GenericException{
//        try {
//            log.info("TaxDepositServiceImpl::insertPayslipTaxInfo service start: employee Id: {}", employee.getId());
//            Optional<Employee> loggedInEmployee = employeeRepository.getLoggedInEmployee();
//
//            Optional<EmployeeTaxDeposit> optionalTaxDeposit = taxDepositRepository.findByEmployeeIdAndFromDateAndToDateAndTaxType(
//                    loggedInEmployee.isPresent() ? loggedInEmployee.get().getId() : employee.getId(),
//                    fromDate, toDate, taxType);
//
//            if (optionalTaxDeposit.isPresent()){
//                return new ServiceResponse(Utils.getSuccessResponse(), optionalTaxDeposit.get());
//            }
//
//            EmployeeTaxDeposit employeeTaxDeposit = new EmployeeTaxDeposit();
//
//            employeeTaxDeposit.setMonthlyPaySlip(monthlyPaySlip);
//            employeeTaxDeposit.setEmployee(employee);
//            employeeTaxDeposit.setTaxType(taxType);
//            employeeTaxDeposit.setAmount(taxToDepositForTheRequestMonth);
//
//            //TODO need to take input chalaNo from input or need to update later
//            employeeTaxDeposit.setChalanNo("ABCD123");
//            employeeTaxDeposit.setComments("Auto deduction of monthly tax by company");
//            employeeTaxDeposit.setFromDate(fromDate);
//            employeeTaxDeposit.setToDate(toDate);
//
//            employeeTaxDeposit.setCreatedBy(1l);
//            employeeTaxDeposit.setCreateTime(LocalDateTime.now());
//
//            employeeTaxDeposit = taxDepositRepository.save(employeeTaxDeposit);
//            log.debug("TaxDepositServiceImpl::insertPayslipTaxInfo employeeTaxDeposit: {}", employeeTaxDeposit.toString());
//
//            log.info("TaxDepositServiceImpl::insertPayslipTaxInfo service end: employee Id: {}", employee.getId());
//            return new ServiceResponse(Utils.getSuccessResponse(), employeeTaxDeposit);
//        }catch (Exception e){
//            log.error("TaxDepositServiceImpl::insertPayslipTaxInfo: Exception occurred while saving tax deposit info!: employee Id: {}", employee.getId());
//            throw new GenericException("Exception occurred while saving tax deposit info!");
//        }
//    }
//
//    //An employee can see his/her tax info only
//    @Override
//    public Page<EmployeeTaxDeposit> getAllTaxInfoByEmployeeId(Long employeeId, Pageable pageable) throws GenericException{
//        try{
//            log.info("TaxDepositServiceImpl::getAllTaxInfoByEmployeeId service start: employee Id: {}", employeeId);
//            Optional<Employee> loggedInEmployee = employeeRepository.getLoggedInEmployee();
//            Page<EmployeeTaxDeposit> employeeTaxDepositPage = taxDepositRepository.findAllByEmployeeId(
//                    loggedInEmployee.isPresent()?loggedInEmployee.get().getId():employeeId, pageable);
//
//            log.debug("TaxDepositServiceImpl::insertPayslipTaxInfo total  number of elements: employee Id: {}",employeeTaxDepositPage.getTotalElements(), employeeId);
//
//            log.info("TaxDepositServiceImpl::insertPayslipTaxInfo service end: employee Id: {}", employeeId);
//            return employeeTaxDepositPage;
//        }catch (Exception e){
//            log.error("TaxDepositServiceImpl::insertPayslipTaxInfo: Exception occurred while getAllTaxInfoByEmployeeId: employee Id: {}", employeeId);
//            throw new GenericException("Exception occurred while getAllTaxInfoByEmployeeId !");
//        }
//
//    }
//    //An employee can see his/her tax info only
//    @Override
//    public Page<EmployeeTaxDeposit> getTaxInfoWithInDateRangeAndEmployeeId(TaxSearchCriteria criteria, Pageable pageable) throws GenericException{
//        try{
//            log.info("TaxDepositServiceImpl::getTaxInfoWithInDateRangeAndEmployeeId service start: criteria: {}", Utils.jsonAsString(criteria));
//            Optional<Employee> optional = employeeRepository.getLoggedInEmployee();
//
//            Optional<Employee> loggedInEmployee = employeeRepository.getLoggedInEmployee();
//            Page<EmployeeTaxDeposit> taxInfoPage = taxDepositRepository.getAllByEmployeeIdAndFromDateAndToDate(
//                    criteria.getFromDate(), criteria.getToDate(), loggedInEmployee.isPresent()?loggedInEmployee.get().getId():criteria.getEmployeeId(), pageable);
//            log.debug("TaxDepositServiceImpl::getTaxInfoWithInDateRangeAndEmployeeId total  number of elements: employee Id: {}", taxInfoPage.getTotalElements(), optional.isPresent()?optional.get().getId():0);
//
//            log.info("TaxDepositServiceImpl::getTaxInfoWithInDateRangeAndEmployeeId service end: criteria: {}", Utils.jsonAsString(criteria));
//            return taxInfoPage;
//        }catch (Exception e){
//            log.error("TaxDepositServiceImpl::getTaxInfoWithInDateRangeAndEmployeeId: Exception occurred while getTaxInfoWithInDateRangeAndEmployeeId");
//            throw new GenericException("Exception occurred while getAllTaxInfoByEmployeeId!");
//        }
//
//    }
//}
