package com.apu.provident.fund.services.payroll;


import com.apu.provident.fund.dto.EmployeeDto;
import com.apu.provident.fund.dto.request.LoginRequestDto;
import com.apu.provident.fund.dto.response.ServiceResponse;
import com.apu.provident.fund.exceptions.GenericException;

public interface LoginService {
    ServiceResponse<EmployeeDto> checkLoginUser(LoginRequestDto loginRequestDto) throws GenericException;

}
