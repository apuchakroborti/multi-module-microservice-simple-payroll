package com.apu.payslip.handler;

import com.apu.payslip.dto.APIResponse;
import com.apu.payslip.dto.ErrorDto;
import com.apu.payslip.exceptions.EmployeeNotFoundException;
import com.apu.payslip.exceptions.GenericException;
import com.apu.payslip.exceptions.PayRollNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class PayRollExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public APIResponse<?> handleMethodArgumentException(MethodArgumentNotValidException exception){
        APIResponse<?> serviceResponse = new APIResponse<>();
        List<ErrorDto> errors = new ArrayList<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> {
                    ErrorDto errorDto = new ErrorDto(error.getField(), error.getDefaultMessage());
                    errors.add(errorDto);
                });
        serviceResponse.setStatus("FAILED");
        serviceResponse.setErrors(errors);
        return serviceResponse;
    }

    @ExceptionHandler(GenericException.class)
    public APIResponse<?> handlerServiceException(GenericException exception){
        APIResponse<?> serviceResponse = new APIResponse<>();
        serviceResponse.setStatus("FAILED");
        serviceResponse.setErrors(Collections.singletonList(new ErrorDto("", exception.getMessage())));
        return serviceResponse;
    }

    @ExceptionHandler(PayRollNotFoundException.class)
    public APIResponse<?> handlerPayRollNotFoundException(PayRollNotFoundException exception){
        APIResponse<?> serviceResponse = new APIResponse<>();
        serviceResponse.setStatus("FAILED");
        serviceResponse.setErrors(Collections.singletonList(new ErrorDto("", exception.getMessage())));
        return serviceResponse;
    }
    @ExceptionHandler(EmployeeNotFoundException.class)
    public APIResponse<?> handlerEmployeeNotFoundException(EmployeeNotFoundException exception){
        APIResponse<?> serviceResponse = new APIResponse<>();
        serviceResponse.setStatus("FAILED");
        serviceResponse.setErrors(Collections.singletonList(new ErrorDto("", exception.getMessage())));
        return serviceResponse;
    }


}
