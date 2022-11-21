package com.apu.employee.services.payroll;

import com.apu.employee.dto.AuthorityModel;
import com.apu.employee.exceptions.GenericException;


public interface AuthorityService {
    AuthorityModel addNewAuthority(AuthorityModel authorityModel) throws GenericException;
}
