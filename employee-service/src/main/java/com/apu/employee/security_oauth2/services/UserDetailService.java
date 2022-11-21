package com.apu.employee.security_oauth2.services;


import com.apu.employee.dto.request.PasswordChangeRequestDto;
import com.apu.employee.dto.request.PasswordResetRequestDto;
import com.apu.employee.dto.response.PasswordChangeResponseDto;
import com.apu.employee.exceptions.EmployeeNotFoundException;
import com.apu.employee.exceptions.GenericException;
import com.apu.employee.entity.payroll.Employee;
import com.apu.employee.repository.payroll.EmployeeRepository;
import com.apu.employee.security_oauth2.models.security.User;
import com.apu.employee.security_oauth2.repository.AuthorityRepository;
import com.apu.employee.security_oauth2.repository.UserRepository;
import com.apu.employee.utils.Defs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;


@Service
public class UserDetailService implements UserService, UserDetailsService {

    Logger logger = LoggerFactory.getLogger(UserDetailService.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository customUserRepository;
    @Autowired
    private AuthorityRepository authorityRepository;

   @Autowired
   @Qualifier("userPasswordEncoder")
   private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        logger.info("loadUserByUsername called");
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if(optionalUser.isPresent()){
            if(!optionalUser.get().isEnabled()){
                throw new UsernameNotFoundException(Defs.USER_INACTIVE+": "+username);
            }
            return optionalUser.get();
        }
        throw new UsernameNotFoundException(Defs.USER_NOT_FOUND+": "+username);
    }

    @Override
    public PasswordChangeResponseDto changeUserPassword(PasswordChangeRequestDto requestDto) throws GenericException {
        try {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String currentUsername = currentUser.getUsername();

            UserDetails userDetails = loadUserByUsername(currentUsername);
            String currentPassword = userDetails.getPassword();
            User user = (User) userDetails;

            if (passwordEncoder.matches(requestDto.getCurrentPassword(), currentPassword)) {
                user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
                userRepository.save(user);
            } else {
                throw new GenericException(Defs.PASSWORD_MISMATCHED);
            }

            return new PasswordChangeResponseDto(true, Defs.PASSWORD_CHANGED_SUCCESSFUL);
        }catch (Exception e){
            logger.error("Error occurred while updating password");
            throw new GenericException("Error occurred while updating password", e);
        }
    }

    @Override
    public PasswordChangeResponseDto resetPassword(PasswordResetRequestDto passwordResetRequestDto) throws GenericException{

        try {
            Optional<Employee> optionalEmployee = customUserRepository.findByEmail(passwordResetRequestDto.getUsername());
            if (!optionalEmployee.isPresent() || optionalEmployee.get().getStatus().equals(false)) {
                throw new EmployeeNotFoundException(Defs.USER_NOT_FOUND);
            }

            UserDetails userDetails = loadUserByUsername(passwordResetRequestDto.getUsername());
            User user = (User) userDetails;

            user.setPassword(passwordEncoder.encode("apu12345"));
            userRepository.save(user);

            return new PasswordChangeResponseDto(true,
                            Defs.PASSWORD_CHANGED_SUCCESSFUL + ": the new Password is: apu12345");
        }catch (Exception e){
            logger.error("Error occurred while resetting password");
            throw new GenericException("Error occurred while resetting password", e);
        }
    }
}