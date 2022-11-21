package com.apu.employee.controllers;

import com.apu.employee.dto.APIResponse;
import com.apu.employee.dto.EmployeeDto;
import com.apu.employee.dto.EmployeeSalaryDto;
import com.apu.employee.dto.Pagination;
import com.apu.employee.dto.request.EmployeeSearchCriteria;
import com.apu.employee.dto.request.PasswordChangeRequestDto;
import com.apu.employee.dto.request.PasswordResetRequestDto;
import com.apu.employee.dto.response.PasswordChangeResponseDto;
import com.apu.employee.entity.payroll.Employee;
import com.apu.employee.entity.payroll.EmployeeSalary;
import com.apu.employee.exceptions.GenericException;
import com.apu.employee.security_oauth2.services.UserService;
import com.apu.employee.services.EmployeeService;
import com.apu.employee.services.payroll.SalaryService;
import com.apu.employee.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/api/employee")
@AllArgsConstructor
@Slf4j
public class EmployeeController {

    private final UserService userService;
    private final EmployeeService employeeService;
    /*
    * This is for enrolling new employee but only admin can create a new employee
    * while creating an employee gross salary must be given
    * monthly draft payslip will be created also
    * salary table will have a new entry for this employee
    * */

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<APIResponse> enrollEmployee(@Valid @RequestBody EmployeeDto employeeDto) throws GenericException {
        log.info("EmployeeController::enrollEmployee request body {}", Utils.jsonAsString(employeeDto));

        EmployeeDto employeeResponseDTO = employeeService.enrollEmployee(employeeDto);
        log.debug(EmployeeController.class.getName()+"::enrollEmployee response {}", Utils.jsonAsString(employeeResponseDTO));

        //Builder Design pattern
        APIResponse<EmployeeDto> responseDTO = APIResponse
                .<EmployeeDto>builder()
                .status("SUCCESS")
                .results(employeeResponseDTO)
                .build();

        log.info("EmployeeController::enrollEmployee response {}", Utils.jsonAsString(responseDTO));

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<APIResponse> searchEmployee(EmployeeSearchCriteria criteria, @PageableDefault(value = 10) Pageable pageable) throws GenericException {
        log.info("EmployeeController::searchEmployee start...");

        Page<Employee>  employeePage = employeeService.getEmployeeList(criteria, pageable);

        List<EmployeeDto> employeeDtoList = Utils.toDtoList(employeePage, EmployeeDto.class);

        APIResponse<List<EmployeeDto>> responseDTO = APIResponse
                .<List<EmployeeDto>>builder()
                .status("SUCCESS")
                .results(employeeDtoList)
                .pagination(new Pagination(employeePage.getTotalElements(), employeePage.getNumberOfElements(), employeePage.getNumber(), employeePage.getSize()))
                .build();

        log.info("EmployeeController::searchEmployee end");
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(path = "/{id}")
    public ResponseEntity<APIResponse> getEmployeeById(@PathVariable(name = "id") Long id ) throws GenericException {
        log.info("EmployeeController::getEmployeeById start...");

        EmployeeDto  employeeDto = employeeService.findEmployeeById(id);

        APIResponse<EmployeeDto> responseDTO = APIResponse
                .<EmployeeDto>builder()
                .status("SUCCESS")
                .results(employeeDto)
                .build();

        log.info("EmployeeController::getEmployeeById end");
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse>  updateEmployeeById(@PathVariable(name = "id") Long id, @RequestBody EmployeeDto employeeBean) throws GenericException {

        log.info("EmployeeController::updateEmployeeById start...");

        EmployeeDto  employeeDto = employeeService.updateEmployeeById(id, employeeBean);

        APIResponse<EmployeeDto> responseDTO = APIResponse
                .<EmployeeDto>builder()
                .status("SUCCESS")
                .results(employeeDto)
                .build();

        log.info("EmployeeController::updateEmployeeById end");
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse> deleteEmployeeById(@PathVariable(name = "id") Long id) throws GenericException {
        log.info("EmployeeController::deleteEmployeeById start...");

        Boolean res = employeeService.deleteEmployeeById(id);

        APIResponse<Boolean> responseDTO = APIResponse
                .<Boolean>builder()
                .status("SUCCESS")
                .results(res)
                .build();

        log.info("EmployeeController::deleteEmployeeById end");
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping("/update-password")
    public ResponseEntity<APIResponse> updatePassword(@RequestBody PasswordChangeRequestDto passwordChangeRequestDto) throws GenericException {
        log.info("EmployeeController::updatePassword start...");

        PasswordChangeResponseDto res = userService.changeUserPassword(passwordChangeRequestDto);

        APIResponse<PasswordChangeResponseDto> responseDTO = APIResponse
                .<PasswordChangeResponseDto>builder()
                .status("SUCCESS")
                .results(res)
                .build();

        log.info("EmployeeController::updatePassword end");
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping("/reset-password")
    public ResponseEntity<APIResponse>  resetPassword(@RequestBody PasswordResetRequestDto passwordResetRequestDto) throws GenericException {
        log.info("EmployeeController::resetPassword start...");

        PasswordChangeResponseDto res = userService.resetPassword(passwordResetRequestDto);

        APIResponse<PasswordChangeResponseDto> responseDTO = APIResponse
                .<PasswordChangeResponseDto>builder()
                .status("SUCCESS")
                .results(res)
                .build();

        log.info("EmployeeController::resetPassword end");
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
    @Autowired
    SalaryService salaryService;

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/salary")
    public ResponseEntity<APIResponse> updateSalary(@Valid @RequestBody EmployeeSalaryDto employeeSalaryDto) throws GenericException {
        log.info("EmployeeController::updateSalary start");

        EmployeeSalaryDto  res = salaryService.updateSalaryData(employeeSalaryDto);

        APIResponse<EmployeeSalaryDto> responseDTO = APIResponse
                .<EmployeeSalaryDto>builder()
                .status("SUCCESS")
                .results(res)
                .build();

        log.info("EmployeeController::updateSalary end");
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/salary")
    public ResponseEntity<APIResponse> getAllSalary(com.apu.employee.dto.request.SalarySearchCriteria searchCriteria,
                                                                         @PageableDefault(value = 12) Pageable pageable) throws GenericException {

        log.info("EmployeeController::getAllSalary start...");
        Page<EmployeeSalary> employeeSalaryPage = salaryService.getSalaryDataWithInDateRangeAndEmployeeId(searchCriteria, pageable);
        List<EmployeeSalaryDto> employeeSalaryDtoList = Utils.toDtoList(employeeSalaryPage, EmployeeSalaryDto.class);

        APIResponse<List<EmployeeSalaryDto>> responseDTO = APIResponse
                .<List<EmployeeSalaryDto>>builder()
                .status("SUCCESS")
                .results(employeeSalaryDtoList)
                .pagination(new Pagination(employeeSalaryPage.getTotalElements(), employeeSalaryPage.getNumberOfElements(), employeeSalaryPage.getNumber(), employeeSalaryPage.getSize()))
                .build();

        log.info("EmployeeController::getAllSalary end");
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/salary/{employeeId}")
    public ResponseEntity<APIResponse> getCurrentSalaryByEmployeeId(@PathVariable("employeeId") Long employeeId) throws GenericException {
        log.info("EmployeeController::getCurrentSalaryByEmployeeId start");

        EmployeeSalaryDto  res = salaryService.getCurrentSalaryByEmployeeId(employeeId);

        APIResponse<EmployeeSalaryDto> responseDTO = APIResponse
                .<EmployeeSalaryDto>builder()
                .status("SUCCESS")
                .results(res)
                .build();

        log.info("EmployeeController::getCurrentSalaryByEmployeeId end");
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
