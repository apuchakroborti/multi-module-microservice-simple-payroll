package com.apu.employee.services.payroll;


import com.apu.employee.dto.EmployeeDto;
import com.apu.employee.dto.request.LoginRequestDto;
import com.apu.employee.exceptions.GenericException;

public interface LoginService {
    EmployeeDto checkLoginUser(LoginRequestDto loginRequestDto) throws GenericException;

}
