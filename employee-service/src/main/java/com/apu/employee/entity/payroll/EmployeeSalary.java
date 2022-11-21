package com.apu.employee.entity.payroll;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "EMPLOYEE_SALARY")
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSalary extends EntityCommon {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_ID", nullable = false)
    private Employee employee;

    @Column(name = "BASIC_SALARY")
    private Double basicSalary;

    @Column(name = "GROSS_SALARY")
    private Double grossSalary;

    private String comments;

    @Column(name = "FROM_DATE", nullable = false)
    private LocalDate fromDate;

    @Column(name = "TO_DATE")
    private LocalDate toDate;

    private Boolean status;
}
