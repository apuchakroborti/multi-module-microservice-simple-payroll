package com.apu.employee.services;

import com.apu.employee.dto.APIResponse;
import com.apu.employee.dto.EmployeeDto;
import com.apu.employee.dto.request.EmployeeSearchCriteria;
import com.apu.employee.dto.request.MonthlyPaySlipJoiningRequestDto;
import com.apu.employee.entity.payroll.Employee;
import com.apu.employee.entity.payroll.EmployeeSalary;
import com.apu.employee.exceptions.EmployeeNotFoundException;
import com.apu.employee.exceptions.GenericException;
import com.apu.employee.repository.payroll.EmployeeRepository;
import com.apu.employee.repository.payroll.EmployeeSalaryRepository;
import com.apu.employee.security_oauth2.models.security.Authority;
import com.apu.employee.security_oauth2.models.security.User;
import com.apu.employee.security_oauth2.repository.AuthorityRepository;
import com.apu.employee.security_oauth2.repository.UserRepository;
import com.apu.employee.specifications.EmployeeSearchSpecifications;
import com.apu.employee.utils.Defs;
import com.apu.employee.utils.Role;
import com.apu.employee.utils.ServiceUtil;
import com.apu.employee.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

//import static com.apu.employee.utils.ServiceUtil.GENERATE_PAYSLIP_WHILE_JOINING;

@Service
@Transactional
@Slf4j
@RefreshScope
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final EmployeeSalaryRepository employeeSalaryRepository;

    @Qualifier("userPasswordEncoder")
    private final PasswordEncoder passwordEncoder;

    @Lazy
    private final RestTemplate template;

    @Value("${microservice.payslip-service.endpoints.endpoint.uri}")
    private String GENERATE_PAYSLIP_WHILE_JOINING;

    @Autowired
    EmployeeServiceImpl(EmployeeRepository employeeRepository,
            UserRepository userRepository,
            AuthorityRepository authorityRepository,
            EmployeeSalaryRepository employeeSalaryRepository,
            @Lazy RestTemplate template,
            @Qualifier("userPasswordEncoder")PasswordEncoder passwordEncoder){
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.employeeSalaryRepository = employeeSalaryRepository;
        this.passwordEncoder = passwordEncoder;
        this.template = template;
    }


    @Override
    @Transactional
    public User addOauthUser(Employee employee, String password) throws GenericException {
        try {
            log.info("EmployeeServiceImpl::addOauthUser start: email: {}", employee.getEmail());
            Optional<User> optionalUser = userRepository.findByUsername(employee.getEmail());
            if (optionalUser.isPresent()) {
                log.error("EmployeeServiceImpl::addOauthUser user already exists: email: {}", employee.getEmail());
                throw new GenericException(Defs.USER_ALREADY_EXISTS);
            }

            User user = new User();
            user.setUsername(employee.getEmail());
            user.setEnabled(true);

            Authority authority = authorityRepository.findByName(Role.EMPLOYEE.getValue());
            user.setAuthorities(Arrays.asList(authority));
            user.setPassword(passwordEncoder.encode(password));

            user = userRepository.save(user);
            log.debug("EmployeeServiceImpl::addOauthUser user: {}", user.toString());
            log.info("EmployeeServiceImpl::addOauthUser end: email: {}", employee.getEmail());
            return user;
        }catch (GenericException e){
            throw e;
        }catch (Exception e){
            log.error("EmployeeServiceImpl::addOauthUser Exception occurred while adding oauth user email:{} message: {}",employee.getEmail(), e.getMessage());
            throw new GenericException("Error occurred while creating oauth user!");
        }
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public EmployeeDto enrollEmployee(EmployeeDto employeeDto) throws GenericException {
        try {
            log.info("EmployeeServiceImpl::enrollEmployee service start: userId: {} and email: {}", employeeDto.getUserId(), employeeDto.getEmail());
            Optional<Employee> optionalEmployee = employeeRepository.findByUserId(employeeDto.getUserId());
            if (optionalEmployee.isPresent()){
                log.error("EmployeeServiceImpl::enrollEmployee service:  userId: {} already exists", employeeDto.getUserId());
                throw new GenericException(Defs.USER_ALREADY_EXISTS);
            }

            Employee employee = new Employee();
            Utils.copyProperty(employeeDto, employee);

            User user = addOauthUser(employee, employeeDto.getPassword());
            log.info("EmployeeServiceImpl::enrollEmployee service:  user: {} ", user.toString());

            employee.setOauthUser(user);
            employee.setStatus(true);

            employee.setCreatedBy(1l);
            employee.setCreateTime(LocalDateTime.now());

            employee = employeeRepository.save(employee);
            log.debug("EmployeeServiceImpl::enrollEmployee service:  employee: {} ", employee.toString());

            EmployeeSalary employeeSalary = new EmployeeSalary();
            employeeSalary.setEmployee(employee);
            employeeSalary.setGrossSalary(employeeDto.getGrossSalary());
            employeeSalary.setBasicSalary(employeeSalary.getGrossSalary() * 0.6);
            employeeSalary.setComments("Joining Salary");
            employeeSalary.setFromDate(employeeDto.getDateOfJoining());
            employeeSalary.setStatus(true);

            employeeSalary.setCreatedBy(1L);
            employeeSalary.setCreateTime(LocalDateTime.now());

            employeeSalary = employeeSalaryRepository.save(employeeSalary);
            log.debug("EmployeeServiceImpl::enrollEmployee service:  employeeSalary: {} ", employeeSalary.toString());


            //generate payslip for the current financial year
            MonthlyPaySlipJoiningRequestDto requestDto = new MonthlyPaySlipJoiningRequestDto();
            requestDto.setEmployeeId(employee.getId());
            requestDto.setGrossSalary(employeeSalary.getGrossSalary());
            requestDto.setJoiningDate(employee.getDateOfJoining());

            log.info("EmployeeServiceImpl::enrollEmployee service: calling generate payslip while joining request: {}", Utils.jsonAsString(requestDto));

            try{
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

            log.debug("EmployeeServiceImpl::enrollEmployee service:  monthly payslip generation successful employee id: {}, email: {} ", employee.getId(), employee.getEmail());

            Utils.copyProperty(employee, employeeDto);
            log.info("EmployeeServiceImpl::enrollEmployee service end: userId: {} and email: {}", employeeDto.getUserId(), employeeDto.getEmail());
            return employeeDto;
        }catch (GenericException e){
            throw e;
        }catch (Exception e){
            log.error("Exception occurred while enrolling employee, message: {}", e.getMessage());
            throw new GenericException(e.getMessage(), e);
        }
    }



    @Override
    public EmployeeDto findByUsername(String username) throws GenericException{
        try {
            log.debug("EmployeeServiceImpl::findByUsername start:  username: {} ", username);
            Optional<Employee> optionalEmployee = employeeRepository.findByEmail(username);
            if (!optionalEmployee.isPresent() || optionalEmployee.get().getStatus().equals(false)) {
                log.debug("EmployeeServiceImpl::findByUsername user not found by  username: {} ", username);
                return null;
            }
            EmployeeDto employeeDto = new EmployeeDto();
            Utils.copyProperty(optionalEmployee.get(), employeeDto);
            log.debug("EmployeeServiceImpl::findByUsername end:  username: {} ", username);
            return employeeDto;
        }catch (Exception e){
            log.error("Error while finding employee by username: {}", username);
            throw new GenericException(e.getMessage());
        }
    }
    @Override
    public EmployeeDto findEmployeeById(Long id) throws GenericException{
        try {
            log.info("EmployeeServiceImpl::findEmployeeById start:  id: {} ", id);
            Optional<Employee> optionalUser = employeeRepository.findById(id);

            if (!optionalUser.isPresent() || optionalUser.get().getStatus().equals(false)) {
                log.debug("EmployeeServiceImpl::findEmployeeById employee not found by id: {} ", id);
                throw new EmployeeNotFoundException(Defs.EMPLOYEE_NOT_FOUND);
            }
            EmployeeDto employeeDto = new EmployeeDto();
            Utils.copyProperty(optionalUser.get(), employeeDto);
            log.info("EmployeeServiceImpl::findEmployeeById end: id: {} ", id);
            return employeeDto;

        }catch (Exception e){
            log.error("Error occurred while fetching employee by id: {}", id);
            throw new GenericException("Error occurred while fetching employee by id", e);
        }
    }

    @Override
    public EmployeeDto updateEmployeeById(Long id, EmployeeDto employeeDto) throws GenericException{
        try {
            log.debug("EmployeeServiceImpl::updateEmployeeById start:  id: {} ", id);

            Optional<Employee> loggedInEmployee = employeeRepository.getLoggedInEmployee();
            if (loggedInEmployee.isPresent() && !loggedInEmployee.get().getId().equals(id)) {
                throw new GenericException("No permission to up update!");
            }

            Optional<Employee> optionalEmployee = employeeRepository.findById(id);
            if (!optionalEmployee.isPresent() || optionalEmployee.get().getStatus().equals(false)){
                log.debug("EmployeeServiceImpl::updateEmployeeById employee not found by id: {} ", id);
                throw new EmployeeNotFoundException(Defs.EMPLOYEE_NOT_FOUND);
            }


            Employee employee = optionalEmployee.get();
            if (!Utils.isNullOrEmpty(employeeDto.getFirstName())) {
                employee.setFirstName(employeeDto.getFirstName());
            }
            if (!Utils.isNullOrEmpty(employeeDto.getLastName())) {
                employee.setLastName(employeeDto.getLastName());
            }
            employee = employeeRepository.save(employee);
            log.debug("EmployeeServiceImpl::updateEmployeeById employee update successful:  employee: {} ", employee.toString());

            Utils.copyProperty(employee, employeeDto);
            log.debug("EmployeeServiceImpl::updateEmployeeById end:  id: {} ", id);

            return employeeDto;
        }catch (Exception e){
         log.error("Exception occurred while updating employee, id: {}", id);
         throw new GenericException(e.getMessage(), e);
        }
    }

    @Override
    public Page<Employee> getEmployeeList(EmployeeSearchCriteria criteria, @PageableDefault(value = 10) Pageable pageable) throws GenericException{
        try {
            log.info("EmployeeServiceImpl::getEmployeeList start:  criteria: {} ", Utils.jsonAsString(criteria));

            Optional<Employee> loggedInEmployee = employeeRepository.getLoggedInEmployee();
            Long id = null;
            if (loggedInEmployee.isPresent()) {
                id = loggedInEmployee.get().getId();
            }

            Page<Employee> userPage = employeeRepository.findAll(
                    EmployeeSearchSpecifications.withId(id == null ? criteria.getId() : id)
                            .and(EmployeeSearchSpecifications.withFirstName(criteria.getFirstName()))
                            .and(EmployeeSearchSpecifications.withLastName(criteria.getLastName()))
                            .and(EmployeeSearchSpecifications.withEmail(criteria.getEmail()))
                            .and(EmployeeSearchSpecifications.withPhone(criteria.getPhone()))
                            .and(EmployeeSearchSpecifications.withStatus(true))
                    , pageable
            );
            log.debug("EmployeeServiceImpl::getEmployeeList number of elements: {} ", userPage.getTotalElements());

            log.info("EmployeeServiceImpl::getEmployeeList end");
            return userPage;
        }catch (Exception e){
            log.error("EmployeeServiceImpl::getEmployeeList exception occurred while fetching user list!");
            throw new GenericException("exception occurred while fetching user list!");
        }
    }

    @Override
    public Boolean deleteEmployeeById(Long id) throws GenericException{
        try {
            log.info("EmployeeServiceImpl::deleteEmployeeById start:  id: {} ", id);

            Optional<Employee> loggedInEmployee = employeeRepository.getLoggedInEmployee();
            Optional<Employee> optionalEmployee = employeeRepository.findById(id);
            if (loggedInEmployee.isPresent() && optionalEmployee.isPresent() &&
                    !loggedInEmployee.get().getId().equals(optionalEmployee.get().getId())) {
                throw new GenericException(Defs.NO_PERMISSION_TO_DELETE);
            }
            if (!optionalEmployee.isPresent()) {
                throw new EmployeeNotFoundException(Defs.EMPLOYEE_NOT_FOUND);
            }

            Employee employee = optionalEmployee.get();
            employee.setStatus(false);

            //soft deletion of employee
            employee = employeeRepository.save(employee);
            User user = employee.getOauthUser();
            user.setEnabled(false);
            userRepository.save(user);

            log.info("EmployeeServiceImpl::deleteEmployeeById end:  id: {} ", id);
            return  true;
        } catch (GenericException e){
            throw e;
        }catch (Exception e){
            log.error("Exception occurred while deleting user, user id: {}", id);
            throw new GenericException(e.getMessage(), e);
        }
    }
}
