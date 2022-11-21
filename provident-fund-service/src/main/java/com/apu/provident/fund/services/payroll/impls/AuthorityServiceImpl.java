package com.apu.provident.fund.services.payroll.impls;

import com.apu.provident.fund.security_oauth2.repository.AuthorityRepository;
import com.apu.provident.fund.services.payroll.AuthorityService;
import com.apu.provident.fund.utils.Utils;
import com.apu.provident.fund.dto.AuthorityModel;
import com.apu.provident.fund.dto.response.ServiceResponse;
import com.apu.provident.fund.exceptions.GenericException;
import com.apu.provident.fund.security_oauth2.models.security.Authority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorityServiceImpl implements AuthorityService {
    Logger logger = LoggerFactory.getLogger(AuthorityServiceImpl.class);


    @Autowired
    AuthorityRepository authorityRepository;

    public ServiceResponse<AuthorityModel> addNewAuthority(AuthorityModel authorityModel) throws GenericException {
        try {
            Authority authority = new Authority();
            Utils.copyProperty(authorityModel, authority);
            authority = authorityRepository.save(authority);
            Utils.copyProperty(authority, authorityModel);
            return new ServiceResponse(Utils.getSuccessResponse(), authorityModel);
        }catch (Exception e){
            logger.error("Exception occurred while adding new authority!");
            throw new GenericException("Exception occurred while adding new authority!", e);
        }
    }
}
