package com.apu.payslip.services.payroll.impls;


import com.apu.payslip.dto.*;
import com.apu.payslip.dto.request.MonthlyPaySlipRequestDto;
import com.apu.payslip.dto.request.PayslipSearchCriteria;
import com.apu.payslip.entity.payroll.MonthlyPaySlip;
import com.apu.payslip.exceptions.GenericException;
import com.apu.payslip.repository.payroll.MonthlyPaySlipRepository;
import com.apu.payslip.services.payroll.MonthlyPaySlipService;
import com.apu.payslip.utils.Defs;
import com.apu.payslip.utils.ServiceUtil;
import com.apu.payslip.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class MonthlyPaySlipServiceImpl implements MonthlyPaySlipService {


    private final MonthlyPaySlipRepository monthlyPaySlipRepository;
    private final RestTemplate template;


    @Override
    public MonthlyPaySlipDto generatePaySlip(MonthlyPaySlipRequestDto requestDto) throws GenericException {
        try {

            //TODO microservice call needed to check the employee valid or not
            /*Optional<Employee> optionalEmployee = employeeRepository.findById(monthlyPaySlipRequestDto.getEmployee().getId());
            if (!optionalEmployee.isPresent()) {
                logger.info("Employee not found with id: {}", monthlyPaySlipRequestDto.getEmployee().getId());
                return new ServiceResponse<>(Utils.getSingleErrorBadRequest(
                        new ArrayList<>(),
                        "employee", Defs.EMPLOYEE_NOT_FOUND,
                        "Please check employee id is correct"), null);
            }*/

            LocalDate currDate = LocalDate.now();
            if (requestDto.getFromDate().isAfter(currDate.with(lastDayOfMonth()))) {
                throw new GenericException("From date must not be after the current date!");
            }

            if (requestDto.getFromDate().getMonth().getValue() != requestDto.getToDate().getMonth().getValue()) {
                throw new GenericException("From date and to date must be within the same month!");
            }

            List<LocalDate> currentFinancialYear = Utils.getCurrentFinancialYear();
            if (requestDto.getFromDate().isBefore(currentFinancialYear.get(0)) || requestDto.getToDate().isAfter(currentFinancialYear.get(1))) {
                throw new GenericException(Defs.PAYSLIP_FROM_TO_DATE_MUST_BE_CURRENT_FINANCIAL_YEAR);
            }

            Page<MonthlyPaySlip> page = monthlyPaySlipRepository.getEmployeeMonthlyPaySlipByDateRangeAndEmployeeId(
                    requestDto.getFromDate(), requestDto.getToDate(), requestDto.getEmployeeId(), PageRequest.of(0, 1));

            //TODO get employee current gross salary by microservice calling
            Double currentGrossSalary = this.getCurrentGrossSalaryFromEMPLOYEE_SERVICE(requestDto);

            MonthlyPaySlip monthlyPaySlip = new MonthlyPaySlip();
            if (page.getTotalElements() == 0) {
                try {
                    //generate payslip for the current financial year
                    this.generatePayslipForCurrentFinancialYear(requestDto.getEmployeeId(), currentGrossSalary, currentFinancialYear.get(0));
                } catch (GenericException e) {
                    log.error("Exception occurred while generating payslip for the current financial year, message: {}", e.getMessage());
                    throw e;
                }

                page = monthlyPaySlipRepository.getEmployeeMonthlyPaySlipByDateRangeAndEmployeeId(
                        requestDto.getFromDate(), requestDto.getToDate(), requestDto.getEmployeeId(), PageRequest.of(0, 1));

                monthlyPaySlip = page.getContent().get(0);

            } else {
                monthlyPaySlip = page.getContent().get(0);
            }
            monthlyPaySlip.setNetPayment(monthlyPaySlip.getGrossSalary());

            //TODO microservice call needed to check monthly tax deduction by company done or not
            Long taxDeductionId = this.getMonthlyTaxDeductionFromTAX_SERVICE(requestDto);

            //TODO microservice call needed to insert provident data
            Long providentFundId = this.getProvidentFundIdFromPROVIDENT_FUND_SERVICE(requestDto);
            monthlyPaySlip.setProvidentFundId(providentFundId);

            monthlyPaySlip = monthlyPaySlipRepository.save(monthlyPaySlip);

            MonthlyPaySlipDto monthlyPaySlipDto = new MonthlyPaySlipDto();
            Utils.copyProperty(monthlyPaySlip, monthlyPaySlipDto);
            log.info("Payslip generated for the employeeId: {}, FromDate: {}, To date: {}",
                    requestDto.getEmployeeId(),
                    requestDto.getFromDate(),
                    requestDto.getToDate());
            return monthlyPaySlipDto;

        }catch (GenericException e){
            throw e;
        }catch (Exception e){
            log.error("Exception occurred while generating payslip, message: {}", e.getMessage());
            throw new GenericException(e.getMessage(), e);
        }
    }
    @Override
    public Page<MonthlyPaySlip> getPaySlipWithInDateRangeAndEmployeeId(PayslipSearchCriteria criteria, Pageable pageable) throws GenericException{
        try {
            Page<MonthlyPaySlip> employeeMonthlyPaySlipPage = monthlyPaySlipRepository.getEmployeeMonthlyPaySlipByDateRangeAndEmployeeId(
                    criteria.getFromDate(), criteria.getToDate(), criteria.getEmployeeId(), pageable);
            return employeeMonthlyPaySlipPage;
        }catch (Exception e){
            log.error("Error occurred while fetching payslip!");
            throw new GenericException(e.getMessage(),e);
        }
    }

    //TODO need to use this calculation where needed
    /*private Double getMonthlyTaxDepositAmount(Employee employee, LocalDate date)throws GenericException{
        try {

            //index 0 = financial year start
            //index 1 = financial year end
            List<LocalDate> currentFinancialYear = Utils.getCurrentFinancialYear();

            Integer remainingNumberOfMonth = Utils.getRemainingMonthForTheCurrentFinancialYear();

            if (date.isAfter(currentFinancialYear.get(1))) {
                throw new GenericException("The date should be within the current financial year");
            }

            //get total taxable Income and total tax the for the current financial year
            Double totalTaxAbleIncome = this.getTotalEstimatedTaxableIncomeBasedOnSalary(employee, currentFinancialYear.get(0), currentFinancialYear.get(1));

            //TODO Microservice call needed
            //get total tax deposition for the current financial year
//            Double totalTaxDeposited = generateTotalTaxDepositForTheCurrentFinancialYear(employee, currentFinancialYear.get(0), currentFinancialYear.get(1));
            Double totalTaxDeposited = 0.0;

            //get the total tax need to pay for this financial year
            Double totalTaxNeedToPay = calculateTotalTaxFromTaxableIncome(totalTaxAbleIncome);

            totalTaxNeedToPay -= totalTaxDeposited;

            return totalTaxNeedToPay/remainingNumberOfMonth;

        }catch (ArithmeticException e){
            throw new GenericException("Arithmetic exception occurred while generating monthly tax calculation, message: "+e.getMessage());
        }catch (NullPointerException e){
            throw new GenericException("Null pointer exception occurred while generating monthly tax calculation, message: "+e.getMessage());
        }
        catch (Exception e){
            throw new GenericException("Internal server error! Contact with admin!, message: "+e.getMessage());
        }
    }*/

    private Double getTotalEstimatedTaxableIncomeBasedOnSalary(Long employeeId, Double employeeSalary, LocalDate fromDate, LocalDate toDate){
       /* EmployeeSalary employeeSalary = employee.getEmployeeSalaryList()
                .stream()
                .filter(salary -> salary.getStatus().equals(true))
                .collect(Collectors.toList()).get(0);*/

        //house rent per year
        final Double HOUSE_RENT_FIXED_EXEMPTION = 12*25000.0;
        final Double HOUSE_RENT_50_PERCENT_OF_BASIC = 12 * employeeSalary*0.60*0.5;
        final Double HOUSE_RENT_EXEMPTION = Math.min(HOUSE_RENT_FIXED_EXEMPTION, HOUSE_RENT_50_PERCENT_OF_BASIC);

        //conveyance per year
        final Double CONVEYANCE_FIXED_EXEMPTION = 300000.0;

        //medical per year
        final Double MEDICAL_FIXED_EXEMPTION = 120000.0;
        final Double MEDICAL_10_PERCENT_OF_BASIC = 12 * employeeSalary*0.60*0.10;//10 percent monthly
        final Double MEDICAL_EXEMPTION = Math.min(MEDICAL_FIXED_EXEMPTION, MEDICAL_10_PERCENT_OF_BASIC);



        try{
            List<MonthlyPaySlip> monthlyPaySlipList = monthlyPaySlipRepository.getEmployeeMonthlyPaySlipByDateRangeAndEmployeeId(
                    fromDate, toDate, employeeId,
                    PageRequest.of(0,12)
            ).getContent();

            Double totalBasic = monthlyPaySlipList
                    .stream()
                    .mapToDouble(o -> o.getBasicSalary()).sum();

            Double totalHouseRent = monthlyPaySlipList
                    .stream()
                    .mapToDouble(o -> o.getHouseRent()).sum();

            Double totalConveyance = monthlyPaySlipList
                    .stream()
                    .mapToDouble(o -> o.getConveyanceAllowance()).sum();

            Double totalMedical = monthlyPaySlipList
                    .stream()
                    .mapToDouble(o -> o.getMedicalAllowance()).sum();

            Double totalArrears = monthlyPaySlipList
                    .stream()
                    .mapToDouble(o -> o.getArrears()).sum();

            Double totalFestivalBonus = monthlyPaySlipList
                    .stream()
                    .mapToDouble(o -> o.getFestivalBonus()).sum();

            //TODO microservice call needed
           /* Double totalPFCompany = monthlyPaySlipList
                    .stream()
                    .mapToDouble(o -> o.getProvidentFund().getCompanyContribution()).sum();*/

            Double totalDue = monthlyPaySlipList
                    .stream()
                    .mapToDouble(o -> o.getDue()).sum();

            Double totalIncentiveBonus = monthlyPaySlipList
                    .stream()
                    .mapToDouble(o -> o.getIncentiveBonus()).sum();

            Double totalOtherPay = monthlyPaySlipList
                    .stream()
                    .mapToDouble(o -> o.getOtherPay()).sum();


            totalHouseRent = (totalHouseRent-HOUSE_RENT_EXEMPTION) < 0.0 ? 0.0 : (totalHouseRent-HOUSE_RENT_EXEMPTION);
            totalConveyance = (totalConveyance-CONVEYANCE_FIXED_EXEMPTION) < 0.0 ? 0.0 : (totalConveyance-CONVEYANCE_FIXED_EXEMPTION);
            totalMedical = (totalMedical-MEDICAL_EXEMPTION) < 0.0 ? 0.0 : (totalMedical-MEDICAL_EXEMPTION);

            Double totalTaxableIncome = totalBasic +
                    totalHouseRent +
                    totalConveyance +
                    totalMedical +
                    totalArrears +
                    totalFestivalBonus +
//                    totalPFCompany +
                    totalDue +
                    totalIncentiveBonus +
                    totalOtherPay;

            return totalTaxableIncome;

        }catch (NullPointerException e){
            log.info("Need to general monthly payslip for this employee:{} for the current financial year", employeeId);
        }catch (Exception e){
            log.info("Internal server error! please contact with admin!");
        }finally {
            return 0.0;
        }
    }
    /*//get total tax deposited
    private Double generateTotalTaxDepositForTheCurrentFinancialYear(Employee employee, LocalDate fromDate, LocalDate toDate) throws GenericException{
        try {
            List<EmployeeTaxDeposit> list = taxDepositRepository.getAllByEmployeeIdAndFromDateAndToDate(
                     fromDate, toDate,employee.getId(), PageRequest.of(0, 100)).getContent();
            Double totalTaxDeposited = list
                                        .stream()
                                        .mapToDouble(o -> o.getAmount())
                                        .sum();

            //TODO need to implement to get the advanced tax from the previous year
            return totalTaxDeposited;
        }catch (NullPointerException e){
            System.out.println("No tax deposition found for this employee for the current financial year");
            return 0.0;
        }
    }*/
    //get total tax from total taxable income as per the rules of  BD
    private Double calculateTotalTaxFromTaxableIncome(Double taxableIncome){

        //get the minimum tax division wise
        //Barishal, Chattogram, Dhaka, Khulna, Rajshahi, Rangpur, Mymensingh and Sylhet.
        //TODO here need to check employee where is he/she living and his tax circle; here I am considering all them are in Dhaka
        List<Double> divisionWiseMinimumTax = new ArrayList<>();
        divisionWiseMinimumTax.addAll(Arrays.asList(
                Defs.MINIMUM_TAX_BARISHAL,
                Defs.MINIMUM_TAX_CHATTOGRAM,
                Defs.MINIMUM_TAX_DHAKA,
                Defs.MINIMUM_TAX_KHULNA,
                Defs.MINIMUM_TAX_RAJSHAHI,
                Defs.MINIMUM_TAX_RANGPUR,
                Defs.MINIMUM_TAX_MYMENSINGH,
                Defs.MINIMUM_TAX_SYLHET));

        taxableIncome-=Defs.TAX_FREE_INCOME_PER_YEAR;

        if(taxableIncome<1.0)return divisionWiseMinimumTax.get(2);

        Double tax = 0.0;

        if((taxableIncome-Defs.PERCENT_5_PER_YEAR_UPTO)<0.0){
            tax += taxableIncome * Defs.PERCENT_5;
            return Math.max(tax, divisionWiseMinimumTax.get(2));
        }
        tax +=Defs.PERCENT_5_PER_YEAR_UPTO * Defs.PERCENT_5;
        taxableIncome-=Defs.PERCENT_5_PER_YEAR_UPTO;

        if((taxableIncome-Defs.PERCENT_10_PER_YEAR_UPTO)<0.0){
            tax += taxableIncome * Defs.PERCENT_10;
            return Math.max(tax, divisionWiseMinimumTax.get(2));
        }
        tax +=Defs.PERCENT_10_PER_YEAR_UPTO * Defs.PERCENT_10;
        taxableIncome-=Defs.PERCENT_10_PER_YEAR_UPTO;

        if((taxableIncome-Defs.PERCENT_15_PER_YEAR_UPTO)<0.0){
            tax += taxableIncome * Defs.PERCENT_15;
            return Math.max(tax, divisionWiseMinimumTax.get(2));
        }
        tax +=Defs.PERCENT_15_PER_YEAR_UPTO * Defs.PERCENT_15;
        taxableIncome-=Defs.PERCENT_15_PER_YEAR_UPTO;

        if((taxableIncome-Defs.PERCENT_20_PER_YEAR_UPTO)<0.0){
            tax += taxableIncome * Defs.PERCENT_20;
            return Math.max(tax, divisionWiseMinimumTax.get(2));
        }
        tax +=Defs.PERCENT_20_PER_YEAR_UPTO *Defs.PERCENT_20;
        taxableIncome-=Defs.PERCENT_20_PER_YEAR_UPTO;

        if(taxableIncome>0){
            tax +=taxableIncome*Defs.PERCENT_25;
            return Math.max(tax, divisionWiseMinimumTax.get(2));
        }
        return tax;
    }
    @Override
    public void generatePayslipForCurrentFinancialYear(Long employeeId, Double employeeSalary, LocalDate fromDate) throws GenericException{
        try {
            LocalDate toDate = null;
            int month = fromDate.getMonthValue();
            int year = fromDate.getYear();

            if (month <= 6) {
                toDate = LocalDate.of(year, 6, 30);
            } else {
                toDate = LocalDate.of(year + 1, 6, 30);
            }
            List<MonthlyPaySlip> monthlyPaySlipList = new ArrayList<>();

            MonthlyPaySlip monthlyPaySlip = new MonthlyPaySlip();
            monthlyPaySlip = getBySettingAllValue(monthlyPaySlip,employeeId, employeeSalary, fromDate, fromDate.with(lastDayOfMonth()));
            monthlyPaySlipList.add(monthlyPaySlip);

            fromDate = fromDate.with(lastDayOfMonth());
            fromDate = fromDate.plusDays(1);

            while (fromDate.isBefore(toDate.plusDays(1))) {
                MonthlyPaySlip monthlyPaySlipNew = new MonthlyPaySlip();
                monthlyPaySlipNew = getBySettingAllValue(monthlyPaySlipNew, employeeId, employeeSalary, fromDate.with(firstDayOfMonth()), fromDate.with(lastDayOfMonth()));
                monthlyPaySlipList.add(monthlyPaySlipNew);

                fromDate = fromDate.with(lastDayOfMonth());
                fromDate = fromDate.plusDays(1);
            }

            monthlyPaySlipRepository.saveAll(monthlyPaySlipList);
        }catch (Exception e){
            throw new GenericException(e.getMessage(), e);
        }
    }

    private MonthlyPaySlip getBySettingAllValue(MonthlyPaySlip monthlyPaySlip, Long employeeId, Double employeeGrossSalary, LocalDate fromDate, LocalDate toDate){
        monthlyPaySlip.setEmployeeId(employeeId);

        monthlyPaySlip.setGrossSalary(employeeGrossSalary);
        monthlyPaySlip.setBasicSalary(employeeGrossSalary*0.60);//60 percent

        monthlyPaySlip.setHouseRent(employeeGrossSalary*0.3);//30 percent
        monthlyPaySlip.setConveyanceAllowance(employeeGrossSalary*0.045);//4.5 percent
        monthlyPaySlip.setMedicalAllowance(employeeGrossSalary*0.055); // 5.5 percent

        monthlyPaySlip.setFestivalBonus(0.0);

        monthlyPaySlip.setDue(0.0);
        monthlyPaySlip.setArrears(0.0);
        monthlyPaySlip.setIncentiveBonus(0.0);
        monthlyPaySlip.setOtherPay(0.0);

        monthlyPaySlip.setProvidentFundId(null);

        monthlyPaySlip.setNetPayment(
                employeeGrossSalary+
                        monthlyPaySlip.getDue()+
                        monthlyPaySlip.getFestivalBonus()+
                        monthlyPaySlip.getIncentiveBonus()+
                        monthlyPaySlip.getOtherPay());

        monthlyPaySlip.setFromDate(fromDate);
        monthlyPaySlip.setToDate(toDate);

        monthlyPaySlip.setCreatedBy(1l);
        monthlyPaySlip.setCreateTime(LocalDateTime.now());
        return monthlyPaySlip;
    }
    private Double getCurrentGrossSalaryFromEMPLOYEE_SERVICE(MonthlyPaySlipRequestDto requestDto) throws GenericException{
        ResponseEntity<APIResponse<EmployeeSalaryDto>> apiEmployeeResponse = null;
        try{

            ParameterizedTypeReference<APIResponse<EmployeeSalaryDto>> typeRef = new ParameterizedTypeReference<APIResponse<EmployeeSalaryDto>>() {
            };

            HttpEntity requestEntity = new HttpEntity(null, Utils.getHeaders());
            apiEmployeeResponse = template.exchange(ServiceUtil.GET_EMP_CURRENT_SALARY+"/"+requestDto.getEmployeeId(), HttpMethod.GET, requestEntity, typeRef);
            log.info("EmployeeServiceImpl::generatePaySlip service: apiResponse: {}", Utils.jsonAsString(apiEmployeeResponse));

            if(!apiEmployeeResponse.getStatusCode().equals(HttpStatus.CREATED)){
                throw new GenericException("Payslip generation while joining not succeed!");
            }else{
                if(apiEmployeeResponse.hasBody() && !apiEmployeeResponse.getBody().getStatus().equals("SUCCESS")){
                    throw new GenericException("Payslip generation while joining not succeed!");
                }
                return apiEmployeeResponse.getBody().getResults().getGrossSalary();
            }
        }catch (Exception e){
            log.error("MonthlyPaySlipServiceImpl::generatePaySlip Exception occurred while generating payslip the current financial year, message: {}", e.getMessage());
            throw new GenericException("Exception occurred while generating payslip the current financial year, message:"+e.getMessage());
        }
    }
    private Long getMonthlyTaxDeductionFromTAX_SERVICE(MonthlyPaySlipRequestDto requestDto) throws GenericException{
        ResponseEntity<APIResponse<EmployeeTaxDepositDto>> apiTaxResponse = null;
        try{

            ParameterizedTypeReference<APIResponse<EmployeeTaxDepositDto>> typeRef = new ParameterizedTypeReference<APIResponse<EmployeeTaxDepositDto>>() {
            };

            EmployeeTaxDepositDto employeeTaxDepositDto = new EmployeeTaxDepositDto();
            employeeTaxDepositDto.setEmployeeId(requestDto.getEmployeeId());
            employeeTaxDepositDto.setFromDate(requestDto.getFromDate());
            employeeTaxDepositDto.setToDate(requestDto.getToDate());


            HttpEntity<EmployeeTaxDepositDto> requestEntity = new HttpEntity(employeeTaxDepositDto, Utils.getHeaders());
            apiTaxResponse = template.exchange(ServiceUtil.SAVE_EMPLOYEE_TAX, HttpMethod.POST, requestEntity, typeRef);
            log.info("EmployeeServiceImpl::generatePaySlip service: "+apiTaxResponse+": {}", Utils.jsonAsString(apiTaxResponse));

            if(!apiTaxResponse.getStatusCode().equals(HttpStatus.CREATED)){
                throw new GenericException("Payslip generation while joining not succeed!");
            }else{
                if(apiTaxResponse.hasBody() && !apiTaxResponse.getBody().getStatus().equals("SUCCESS")){
                    throw new GenericException("Payslip generation while joining not succeed!");
                }
                return apiTaxResponse.getBody().getResults().getId();
            }
        }catch (Exception e){
            log.error("MonthlyPaySlipServiceImpl::generatePaySlip Exception occurred while generating payslip the current financial year, message: {}", e.getMessage());
            throw new GenericException("Exception occurred while generating payslip the current financial year, message:"+e.getMessage());
        }
    }
    private Long getProvidentFundIdFromPROVIDENT_FUND_SERVICE(MonthlyPaySlipRequestDto requestDto) throws GenericException{
        ResponseEntity<APIResponse<ProvidentFundDto>> apiProvidentFundResponse = null;
        try{

            ParameterizedTypeReference<APIResponse<ProvidentFundDto>> typeRef = new ParameterizedTypeReference<APIResponse<ProvidentFundDto>>() {
            };

            ProvidentFundDto providentFundDto = new ProvidentFundDto();
            providentFundDto.setEmployeeId(requestDto.getEmployeeId());
            providentFundDto.setGrossSalary(100.0);

            HttpEntity requestEntity = new HttpEntity(null, Utils.getHeaders());
            apiProvidentFundResponse = template.exchange(ServiceUtil.SAVE_PROVIDENT_FUND, HttpMethod.POST, requestEntity, typeRef);
            log.info("EmployeeServiceImpl::generatePaySlip service: apiResponse: {}", Utils.jsonAsString(apiProvidentFundResponse));

            if(!apiProvidentFundResponse.getStatusCode().equals(HttpStatus.CREATED)){
                throw new GenericException("Payslip generation while joining not succeed!");
            }else{
                if(apiProvidentFundResponse.hasBody() && !apiProvidentFundResponse.getBody().getStatus().equals("SUCCESS")){
                    throw new GenericException("Payslip generation while joining not succeed!");
                }
                return apiProvidentFundResponse.getBody().getResults().getId();
            }
        }catch (Exception e){
            log.error("MonthlyPaySlipServiceImpl::generatePaySlip Exception occurred while generating payslip the current financial year, message: {}", e.getMessage());
            throw new GenericException("Exception occurred while generating payslip the current financial year, message:"+e.getMessage());
        }
    }
}
