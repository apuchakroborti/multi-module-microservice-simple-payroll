package com.apu.payslip.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyPaySlipRequestDto {
    private Long id;

    private Long employeeId;

//    private Double grossSalary;

    @NotNull(message = "From date should not be null!")
    private LocalDate fromDate;

    @NotNull(message = "To date should not be null!")
    private LocalDate toDate;
}
