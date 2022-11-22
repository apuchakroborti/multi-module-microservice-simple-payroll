package com.apu.provident.fund.controllers;

import com.apu.provident.fund.dto.ProvidentFundDto;
import com.apu.provident.fund.dto.request.ProvidentFundSearchCriteria;
import com.apu.provident.fund.dto.response.APIResponse;
import com.apu.provident.fund.dto.response.Pagination;
import com.apu.provident.fund.entity.payroll.ProvidentFund;
import com.apu.provident.fund.exceptions.GenericException;
import com.apu.provident.fund.services.payroll.ProvidentFundService;
import com.apu.provident.fund.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/provident-fund")
@Slf4j
public class ProvidentFundController {
    @Autowired
    ProvidentFundService employeeProvidentFundService;

    @GetMapping
    public ResponseEntity<APIResponse> getPFData(ProvidentFundSearchCriteria criteria, @PageableDefault(value = 12) Pageable pageable) throws GenericException {
        log.info("ProvidentFundController::getPFData request: {}", Utils.jsonAsString(criteria));

        Page<ProvidentFund> providentFundPage = employeeProvidentFundService.getPFInfoWithSearchCriteria(criteria, pageable);
        List<ProvidentFundDto> providentFundDtoList = Utils.toDtoList(providentFundPage, ProvidentFundDto.class);

        APIResponse<List<ProvidentFundDto>> responseDTO = APIResponse
                .<List<ProvidentFundDto>>builder()
                .status("SUCCESS")
                .results(providentFundDtoList)
                .pagination(new Pagination(providentFundPage.getTotalElements(), providentFundPage.getNumberOfElements(), providentFundPage.getNumber(), providentFundPage.getSize()))
                .build();

        log.info("ProvidentFundController::getPFData response: {}", Utils.jsonAsString(responseDTO));
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<APIResponse> savePFData(@Valid @RequestBody ProvidentFundDto providentFundDto) throws GenericException {
        log.info("ProvidentFundController::savePFData request: {}", Utils.jsonAsString(providentFundDto));

        ProvidentFundDto result = employeeProvidentFundService.savePfData(providentFundDto);

        APIResponse<ProvidentFundDto> responseDTO = APIResponse
                .<ProvidentFundDto>builder()
                .status("SUCCESS")
                .results(result)
                .build();

        log.info("ProvidentFundController::getPFData response: {}", Utils.jsonAsString(responseDTO));
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
