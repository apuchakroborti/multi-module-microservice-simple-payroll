package com.apu.employee.dto.request;

import com.apu.employee.dto.EmployeeDto;
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

    @NotNull(message = "Employee id should not be null!")
    private EmployeeDto employee;

    @NotNull(message = "From date should not be null!")
    private LocalDate fromDate;

    @NotNull(message = "To date should not be null!")
    private LocalDate toDate;
}
