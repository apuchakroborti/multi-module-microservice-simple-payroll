package com.apu.provident.fund.controllers;

import com.apu.provident.fund.dto.ProvidentFundDto;
import com.apu.provident.fund.dto.request.ProvidentFundSearchCriteria;
import com.apu.provident.fund.dto.response.Pagination;
import com.apu.provident.fund.dto.response.ServiceResponse;
import com.apu.provident.fund.entity.payroll.ProvidentFund;
import com.apu.provident.fund.exceptions.GenericException;
import com.apu.provident.fund.services.payroll.ProvidentFundService;
import com.apu.provident.fund.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/provident-fund")
public class ProvidentFundController {
    @Autowired
    ProvidentFundService employeeProvidentFundService;
    @GetMapping
    public ServiceResponse<Page<ProvidentFundDto>> getPFData(ProvidentFundSearchCriteria criteria, @PageableDefault(value = 12) Pageable pageable) throws GenericException {

        Page<ProvidentFund> providentFundPage = employeeProvidentFundService.getPFInfoWithSearchCriteria(criteria, pageable);

        return new ServiceResponse(Utils.getSuccessResponse(),
                Utils.toDtoList(providentFundPage, ProvidentFundDto.class),
                new Pagination(providentFundPage.getTotalElements(),
                        providentFundPage.getNumberOfElements(),
                        providentFundPage.getNumber(),
                        providentFundPage.getSize()));
    }
}
