package com.apu.employee.services.payroll.impls;

import com.apu.employee.dto.EmployeeDto;
import com.apu.employee.dto.request.LoginRequestDto;
import com.apu.employee.entity.payroll.Employee;
import com.apu.employee.exceptions.GenericException;
import com.apu.employee.repository.payroll.EmployeeRepository;
import com.apu.employee.security_oauth2.models.security.User;
import com.apu.employee.security_oauth2.repository.UserRepository;
import com.apu.employee.services.payroll.LoginService;
import com.apu.employee.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginServiceImpl implements LoginService {

    Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    @Qualifier("userPasswordEncoder")
    private PasswordEncoder passwordEncoder;

    @Override
    public EmployeeDto checkLoginUser(LoginRequestDto loginRequestDto) throws GenericException {
        try {
            EmployeeDto employeeDto = new EmployeeDto();

            Optional<User> optionalUser = userRepository.findByUsername(loginRequestDto.getUsername());
            if (optionalUser.isPresent()) {
                if (passwordEncoder.matches(loginRequestDto.getPassword(), optionalUser.get().getPassword())) {
                    Optional<Employee> optionalEmployee = employeeRepository.findByEmail(loginRequestDto.getUsername());
                    if (optionalEmployee.isPresent()) {
                        Utils.copyProperty(optionalEmployee.get(), employeeDto);
                    } else {
                        employeeDto.setFirstName("Admin");
                        employeeDto.setLastName("Admin");
                    }
                } else {
                    throw new GenericException("Username or password is incorrect!");
                }
            } else {
                throw new GenericException("Username or password is incorrect!");
            }
            return employeeDto;
        }catch (Exception e){
            logger.error("Exception occurred while checking login user, message: {}", e.getMessage());
            throw new GenericException(e.getMessage(), e);
        }
    }
}
