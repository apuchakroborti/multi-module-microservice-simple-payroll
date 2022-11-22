package com.apu.employee.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyPaySlipJoiningRequestDto {
    private Long employeeId;
    private Double grossSalary;
    @NotNull(message = "Joining date should not be null!")
    private LocalDate joiningDate;
}
