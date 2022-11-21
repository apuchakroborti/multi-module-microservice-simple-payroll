package com.example.payroll.entity.payroll;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

//using @Getter and @Setter instead of @Data to remove remove recursive toString and EqualsAndHashCode method
@Entity
@Getter
@Setter
@Table(name = "EMPLOYEE_PROVIDENT_FUND")
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "employee")
@EqualsAndHashCode(exclude = "employee")
public class ProvidentFund extends EntityCommon {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_ID", nullable = false)
    private Employee employee;

    @Column(name = "EMPLOYEE_CONTRIBUTION")
    private Double employeeContribution;

    @Column(name = "COMPANY_CONTRIBUTION")
    private Double companyContribution;

    private String comments;

    @Column(name = "FROM_DATE", nullable = false)
    private LocalDate fromDate;

    @Column(name = "TO_DATE", nullable = false)
    private LocalDate toDate;

    @OneToOne(mappedBy = "providentFund")
    private MonthlyPaySlip monthlyPaySlip;
}
