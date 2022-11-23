package com.apu.employee.services.payroll.impls;

import com.apu.employee.dto.APIResponse;
import com.apu.employee.dto.EmployeeSalaryDto;
import com.apu.employee.dto.request.MonthlyPaySlipJoiningRequestDto;
import com.apu.employee.dto.request.SalarySearchCriteria;
import com.apu.employee.exceptions.EmployeeNotFoundException;
import com.apu.employee.exceptions.GenericException;
import com.apu.employee.entity.payroll.Employee;
import com.apu.employee.entity.payroll.EmployeeSalary;
import com.apu.employee.repository.payroll.EmployeeRepository;
import com.apu.employee.repository.payroll.EmployeeSalaryRepository;
import com.apu.employee.services.payroll.SalaryService;
import com.apu.employee.utils.Defs;
import com.apu.employee.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

//import static com.apu.employee.utils.ServiceUtil.GENERATE_PAYSLIP_WHILE_JOINING;


@Service
@Slf4j
@RefreshScope
public class SalaryServiceImpl implements SalaryService {
//    Logger log = LoggerFactory.getLogger(SalaryServiceImpl.class);

    private final EmployeeSalaryRepository employeeSalaryRepository;
    private final EmployeeRepository employeeRepository;
    @Lazy
    private final RestTemplate template;

    @Value("${microservice.payslip-service.endpoints.endpoint.uri}")
    private String GENERATE_PAYSLIP_WHILE_JOINING;

    @Autowired
    SalaryServiceImpl(EmployeeSalaryRepository employeeSalaryRepository,
             EmployeeRepository employeeRepository, RestTemplate template){
        this.employeeSalaryRepository = employeeSalaryRepository;
        this.employeeRepository = employeeRepository;
        this.template = template;
    }


    @Override
    public EmployeeSalaryDto updateSalaryData(EmployeeSalaryDto employeeSalaryDto) throws GenericException {
        try {
            //TODO check fromDate must be after the month of joining date
            EmployeeSalary employeeSalary = employeeSalaryRepository.getEmployeeCurrentSalaryByEmployeeId(employeeSalaryDto.getEmployee().getId());
            Employee employee = null;
            if (employeeSalary != null) {
                employeeSalary.setToDate(employeeSalaryDto.getFromDate().minusDays(1));
                employeeSalary.setStatus(false);
                employee = employeeSalary.getEmployee();
                employeeSalaryRepository.save(employeeSalary);
            } else {
                Optional<Employee> optionalEmployee = employeeRepository.findById(employeeSalaryDto.getEmployee().getId());
                if (!optionalEmployee.isPresent()) {
                    throw new EmployeeNotFoundException(Defs.EMPLOYEE_NOT_FOUND);
                }
                employeeSalaryDto.setFromDate(optionalEmployee.get().getDateOfJoining());
                if (employee == null) employee = optionalEmployee.get();
            }
            EmployeeSalary employeeSalaryNew = new EmployeeSalary();
            Utils.copyProperty(employeeSalaryDto, employeeSalaryNew);

            Double basic = employeeSalaryDto.getGrossSalary() * 60 / 100.0;
            employeeSalaryNew.setBasicSalary(basic);
            employeeSalaryNew.setStatus(true);

            employeeSalaryNew.setCreatedBy(1L);
            employeeSalaryNew.setCreateTime(LocalDateTime.now());
            employeeSalaryNew.setEmployee(employee);
            employeeSalaryNew = employeeSalaryRepository.save(employeeSalaryNew);
            Utils.copyProperty(employeeSalaryNew, employeeSalaryDto);

            //TODO microservice call needed
            //update payslip's info from this month to last month for this financial year
            try{
                MonthlyPaySlipJoiningRequestDto requestDto = new MonthlyPaySlipJoiningRequestDto();
                requestDto.setEmployeeId(employee.getId());
                requestDto.setGrossSalary(employeeSalary.getGrossSalary());
                requestDto.setJoiningDate(employee.getDateOfJoining());

                ResponseEntity<APIResponse<Boolean>> apiResponse = null;
                ParameterizedTypeReference<APIResponse<Boolean>> typeRef = new ParameterizedTypeReference<APIResponse<Boolean>>() {
                };

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);


                headers = new HttpHeaders();
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<MonthlyPaySlipJoiningRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);
                apiResponse = template.exchange(GENERATE_PAYSLIP_WHILE_JOINING, HttpMethod.POST, requestEntity, typeRef);
                log.info("EmployeeServiceImpl::enrollEmployee service: apiResponse: {}", Utils.jsonAsString(apiResponse));

                if(!apiResponse.getStatusCode().equals(HttpStatus.CREATED)){
                    throw new GenericException("Payslip generation while joining not succeed!");
                }else{
                    if(apiResponse.hasBody() && !apiResponse.getBody().getStatus().equals("SUCCESS")){
                        throw new GenericException("Payslip generation while joining not succeed!");
                    }
                }
            }catch (Exception e){
                log.error("EmployeeServiceImpl::enrollEmployee Exception occurred while generating payslip the current financial year, message: {}", e.getMessage());
                throw new GenericException("Exception occurred while generating payslip the current financial year, message:"+e.getMessage());
            }

            return employeeSalaryDto;
        }catch (Exception e){
            log.error("Error occurred while update salary by employee id: {}", employeeSalaryDto.getEmployee().getId());
            throw new GenericException(e.getMessage(), e);
        }
    }
    @Override
    public Page<EmployeeSalary> getSalaryDataWithInDateRangeAndEmployeeId(SalarySearchCriteria searchCriteria, Pageable pageable)throws GenericException{
        try {
            Page<EmployeeSalary> employeeSalaryPage = employeeSalaryRepository.getEmployeeSalaryByDateRangeAndEmployeeId(
                    searchCriteria.getFromDate(),
                    searchCriteria.getToDate(),
                    searchCriteria.getEmployeeId(),
                    pageable);
            return employeeSalaryPage;
        }catch (Exception e){
            log.error("Error occurred while fetching salary");
            throw new GenericException("Internal server error", e);
        }
    }
    @Override
    public EmployeeSalaryDto getCurrentSalaryByEmployeeId(Long employeeId) throws GenericException {
        try {

            EmployeeSalary employeeCurrentSalary = employeeSalaryRepository.getEmployeeCurrentSalaryByEmployeeId(employeeId);
            EmployeeSalaryDto employeeSalaryDto = new EmployeeSalaryDto();
            Utils.copyProperty(employeeCurrentSalary, employeeSalaryDto);

            return employeeSalaryDto;
        }catch (Exception e){
            log.error("Error occurred while fetching current salary of an employee id: {}", employeeId);
            throw new GenericException(e.getMessage(), e);
        }
    }
}
