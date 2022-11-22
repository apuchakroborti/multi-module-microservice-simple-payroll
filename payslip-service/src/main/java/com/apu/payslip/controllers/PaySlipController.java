package com.apu.payslip.controllers;

import com.apu.payslip.dto.APIResponse;
import com.apu.payslip.dto.MonthlyPaySlipDto;
import com.apu.payslip.dto.Pagination;
import com.apu.payslip.dto.request.MonthlyPaySlipJoiningRequestDto;
import com.apu.payslip.dto.request.MonthlyPaySlipRequestDto;
import com.apu.payslip.dto.request.PayslipSearchCriteria;
import com.apu.payslip.entity.payroll.MonthlyPaySlip;
import com.apu.payslip.exceptions.GenericException;
import com.apu.payslip.services.payroll.MonthlyPaySlipService;
import com.apu.payslip.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/payslip")
@Slf4j
public class PaySlipController {

    @Autowired
    MonthlyPaySlipService monthlyPaySlipService;

    /*
    * Using this API you can generate monthly final payslip for any month from current or previous financial year
    * Monthly draft payslip will be generate for the current financial year but final for the starting of the financial year to current month
    * Tax and Provident fund will be deducted from salary and will be inserted into TaxDeposit and Provident fund table respectively
    * */
//    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<APIResponse> generatePaySlip(@Valid @RequestBody MonthlyPaySlipRequestDto requestDto) throws GenericException {
        log.info("PaySlipController::generatePaySlip request: {}", Utils.jsonAsString(requestDto));
        MonthlyPaySlipDto monthlyPaySlipDto = monthlyPaySlipService.generatePaySlip(requestDto);
        APIResponse<MonthlyPaySlipDto> responseDTO = APIResponse
                .<MonthlyPaySlipDto>builder()
                .status("SUCCESS")
                .results(monthlyPaySlipDto)
                .build();

        log.info("PaySlipController::generatePaySlip response {}", Utils.jsonAsString(responseDTO));
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
    @PostMapping("/join")
    public ResponseEntity<APIResponse> generatePaySlipWhileJoining(@Valid @RequestBody MonthlyPaySlipJoiningRequestDto requestDto) throws GenericException {
        log.info("PaySlipController::generatePaySlip request: {}", Utils.jsonAsString(requestDto));
        monthlyPaySlipService.generatePayslipForCurrentFinancialYear(
                requestDto.getEmployeeId(),
                requestDto.getGrossSalary(),
                requestDto.getJoiningDate());

        APIResponse<Boolean> responseDTO = APIResponse
                .<Boolean>builder()
                .status("SUCCESS")
                .results(true)
                .build();

        log.info("PaySlipController::generatePaySlip response {}", Utils.jsonAsString(responseDTO));
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

//    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    @GetMapping
    public ResponseEntity<APIResponse>  getPaySlipBySearchCriteria(PayslipSearchCriteria criteria, @PageableDefault(value = 12) Pageable pageable) throws GenericException{
        log.info("PaySlipController::getPaySlipBySearchCriteria: criteria: {}", Utils.jsonAsString(criteria));

        Page<MonthlyPaySlip> payslipPage = monthlyPaySlipService.getPaySlipWithInDateRangeAndEmployeeId(criteria, pageable);
        List<MonthlyPaySlipDto> employeeDtoList = Utils.toDtoList(payslipPage, MonthlyPaySlipDto.class);
        APIResponse<List<MonthlyPaySlipDto>> responseDTO = APIResponse
                .<List<MonthlyPaySlipDto>>builder()
                .status("SUCCESS")
                .results(employeeDtoList)
                .pagination(new Pagination(payslipPage.getTotalElements(), payslipPage.getNumberOfElements(), payslipPage.getNumber(), payslipPage.getSize()))
                .build();

        log.info("PaySlipController::getPaySlipBySearchCriteria: response: {}", Utils.jsonAsString(responseDTO));
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
