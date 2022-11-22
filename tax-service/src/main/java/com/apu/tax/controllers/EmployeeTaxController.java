package com.apu.tax.controllers;

import com.apu.tax.dto.TaxDepositDto;
import com.apu.tax.dto.request.TaxSearchCriteria;
import com.apu.tax.dto.response.APIResponse;
import com.apu.tax.dto.response.Pagination;
import com.apu.tax.entity.payroll.EmployeeTaxDeposit;
import com.apu.tax.exceptions.GenericException;
import com.apu.tax.services.payroll.TaxDepositService;
import com.apu.tax.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/employee/tax")
@AllArgsConstructor
@Slf4j
public class EmployeeTaxController {

    private final TaxDepositService taxDepositService;

    @PostMapping
    public ResponseEntity<APIResponse> insertTaxInfo(@Valid @RequestBody TaxDepositDto taxDepositDto) throws GenericException {
        log.info("EmployeeTaxController::insertTaxInfo request body {}", Utils.jsonAsString(taxDepositDto));

        TaxDepositDto employeeResponseDTO = taxDepositService.insertIndividualTaxInfo(taxDepositDto);
        log.debug("EmployeeTaxController::insertTaxInfo response {}", Utils.jsonAsString(employeeResponseDTO));

        //Builder Design pattern
        APIResponse<TaxDepositDto> responseDTO = APIResponse
                .<TaxDepositDto>builder()
                .status("SUCCESS")
                .results(employeeResponseDTO)
                .build();

        log.info("EmployeeController::insertTaxInfo response {}", Utils.jsonAsString(responseDTO));
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
    @GetMapping("/{employeeId}")
    public ResponseEntity<APIResponse> getAllTaxInfoByEmployeeId(@PathVariable("employeeId") Long employeeId, @PageableDefault(value = 12) Pageable pageable) throws GenericException{
        log.info("EmployeeTaxController::getAllTaxInfoByEmployeeId requested employee id: {}", employeeId);

        Page<EmployeeTaxDeposit> taxDepositPage = taxDepositService.getAllTaxInfoByEmployeeId(employeeId, pageable);
        List<TaxDepositDto> employeeTaxDepositDtoList = Utils.toDtoList(taxDepositPage, TaxDepositDto.class);

        APIResponse<List<TaxDepositDto>> responseDTO = APIResponse
                .<List<TaxDepositDto>>builder()
                .status("SUCCESS")
                .results(employeeTaxDepositDtoList)
                .pagination(new Pagination(taxDepositPage.getTotalElements(), taxDepositPage.getNumberOfElements(), taxDepositPage.getNumber(), taxDepositPage.getSize()))
                .build();

        log.info("EmployeeTaxController::getAllTaxInfoByEmployeeId response: {}", Utils.jsonAsString(responseDTO));
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<APIResponse> getAllTaxInfo(TaxSearchCriteria criteria, @PageableDefault(value = 12) Pageable pageable) throws GenericException{
        log.info("EmployeeTaxController::getAllTaxInfo request criteria: {}", Utils.jsonAsString(criteria));
        Page<EmployeeTaxDeposit> taxDepositPage = taxDepositService.getTaxInfoWithInDateRangeAndEmployeeId(criteria, pageable);

        List<TaxDepositDto> employeeTaxDepositDtoList = Utils.toDtoList(taxDepositPage, TaxDepositDto.class);

        APIResponse<List<TaxDepositDto>> responseDTO = APIResponse
                .<List<TaxDepositDto>>builder()
                .status("SUCCESS")
                .results(employeeTaxDepositDtoList)
                .pagination(new Pagination(taxDepositPage.getTotalElements(), taxDepositPage.getNumberOfElements(), taxDepositPage.getNumber(), taxDepositPage.getSize()))
                .build();

        log.info("EmployeeTaxController::getAllTaxInfo response: {}", Utils.jsonAsString(responseDTO));
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
