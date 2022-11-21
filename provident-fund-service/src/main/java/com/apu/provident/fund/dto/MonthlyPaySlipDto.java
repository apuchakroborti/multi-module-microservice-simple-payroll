package com.apu.provident.fund.dto;

import com.apu.provident.fund.entity.payroll.EntityCommon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyPaySlipDto extends EntityCommon {
    private Long id;
    private EmployeeDto employee;
    private Double grossSalary;
    private Double basicSalary;
    private Double houseRent;
    private Double conveyanceAllowance;
    private Double medicalAllowance;
    private Double due;

    private ProvidentFundDto providentFund;
    private List<EmployeeTaxDepositDto> employeeTaxDepositList;

    private Double arrears;
    private Double festivalBonus;
    private Double incentiveBonus;
    private Double otherPay;
    private Double netPayment;
    private String status;
    private String comments;
    private LocalDate fromDate;
    private LocalDate toDate;
}
