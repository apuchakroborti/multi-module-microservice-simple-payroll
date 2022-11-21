package com.apu.employee.dto.request;

import lombok.Data;

@Data
public class PasswordChangeRequestDto {

    private String currentPassword;

    private String newPassword;
}