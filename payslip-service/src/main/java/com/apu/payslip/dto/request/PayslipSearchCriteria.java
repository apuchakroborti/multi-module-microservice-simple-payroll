package com.apu.payslip.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayslipSearchCriteria {
    private Long employeeId;
    private LocalDate fromDate;
    private LocalDate toDate;
}
