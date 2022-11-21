package com.apu.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvidentFundDto extends CommonDto {
    private Long id;
    private EmployeeDto employee;
    private Double employeeContribution;
    private Double companyContribution;
    private String comments;
    private LocalDate fromDate;
    private LocalDate toDate;
}
