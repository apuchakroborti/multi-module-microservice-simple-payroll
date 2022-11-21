package com.apu.provident.fund.services.payroll;

import com.apu.provident.fund.dto.AuthorityModel;
import com.apu.provident.fund.dto.response.ServiceResponse;
import com.apu.provident.fund.exceptions.GenericException;


public interface AuthorityService {
    ServiceResponse<AuthorityModel> addNewAuthority(AuthorityModel authorityModel) throws GenericException;
}
