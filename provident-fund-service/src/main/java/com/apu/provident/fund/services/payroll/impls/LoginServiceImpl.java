package com.apu.provident.fund.services.payroll.impls;

import com.apu.provident.fund.security_oauth2.repository.UserRepository;
import com.apu.provident.fund.services.payroll.LoginService;
import com.apu.provident.fund.utils.Utils;
import com.apu.provident.fund.dto.EmployeeDto;
import com.apu.provident.fund.dto.request.LoginRequestDto;
import com.apu.provident.fund.dto.response.ServiceResponse;
import com.apu.provident.fund.exceptions.GenericException;
import com.apu.provident.fund.repository.payroll.EmployeeRepository;
import com.apu.provident.fund.security_oauth2.models.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public ServiceResponse<EmployeeDto> checkLoginUser(LoginRequestDto loginRequestDto) throws GenericException {
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
                    return new ServiceResponse<>(Utils.getSingleErrorBadRequest(
                            new ArrayList<>(),
                            "username, password", null,
                            "Please check username or password is incorrect"), null);
                }
            } else {
                return new ServiceResponse<>(Utils.getSingleErrorBadRequest(
                        new ArrayList<>(),
                        "username, password", null,
                        "Please check username or password is incorrect"), null);
            }
            return new ServiceResponse(Utils.getSuccessResponse(), employeeDto);
        }catch (Exception e){
            logger.error("Exception occurred while checking login user, message: {}", e.getMessage());
            throw new GenericException(e.getMessage(), e);
        }
    }
}
