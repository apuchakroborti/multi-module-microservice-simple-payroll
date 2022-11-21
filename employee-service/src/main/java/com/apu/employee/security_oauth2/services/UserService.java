package com.apu.employee.security_oauth2.services;


import com.apu.employee.dto.request.PasswordChangeRequestDto;
import com.apu.employee.dto.request.PasswordResetRequestDto;
import com.apu.employee.dto.response.PasswordChangeResponseDto;
import com.apu.employee.exceptions.GenericException;

public interface UserService {
    PasswordChangeResponseDto changeUserPassword(PasswordChangeRequestDto passwordChangeRequestDto) throws GenericException;
    PasswordChangeResponseDto resetPassword(PasswordResetRequestDto passwordResetRequestDto) throws GenericException;
}