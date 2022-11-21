package com.apu.employee.services.payroll.impls;

import com.apu.employee.dto.AuthorityModel;
import com.apu.employee.exceptions.GenericException;
import com.apu.employee.security_oauth2.models.security.Authority;
import com.apu.employee.security_oauth2.repository.AuthorityRepository;
import com.apu.employee.services.payroll.AuthorityService;
import com.apu.employee.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorityServiceImpl implements AuthorityService {
    Logger logger = LoggerFactory.getLogger(AuthorityServiceImpl.class);


    @Autowired
    AuthorityRepository authorityRepository;

    public AuthorityModel addNewAuthority(AuthorityModel authorityModel) throws GenericException {
        try {
            Authority authority = new Authority();
            Utils.copyProperty(authorityModel, authority);
            authority = authorityRepository.save(authority);
            Utils.copyProperty(authority, authorityModel);
            return authorityModel;
        }catch (Exception e){
            logger.error("Exception occurred while adding new authority!");
            throw new GenericException("Exception occurred while adding new authority!", e);
        }
    }
}
