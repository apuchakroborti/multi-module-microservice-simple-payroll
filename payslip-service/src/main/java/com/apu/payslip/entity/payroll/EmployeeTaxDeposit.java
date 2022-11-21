//package com.example.payroll.entity.payroll;
//
//import com.example.payroll.utils.TaxType;
//import lombok.*;
//
//import javax.persistence.*;
//import java.time.LocalDate;
//
////using @Getter and @Setter instead of @Data to remove remove recursive toString and EqualsAndHashCode method
//@Entity
//@Getter
//@Setter
//@Table(name = "EMPLOYEE_TAX_DEPOSIT")
//@NoArgsConstructor
//@AllArgsConstructor
//@ToString(exclude = "employee")
//@EqualsAndHashCode(exclude = "employee")
//public class EmployeeTaxDeposit extends EntityCommon {
//    @Id
//    @GeneratedValue(strategy= GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "MONTHLY_PAYSLIP_ID", nullable = false)
//    private MonthlyPaySlip monthlyPaySlip;
//
//    @ManyToOne
//    @JoinColumn(name = "EMPLOYEE_ID", nullable = false)
//    private Employee employee;
//
//    @Column(name = "TAX_TYPE", nullable = false)
//    private TaxType taxType;
//
//    @Column(name = "AMOUNT", nullable = false)
//    private Double amount;
//
//    @Column(name = "CHALAN_NO", nullable = false)
//    private String chalanNo;
//
//    private String comments;
//
//    @Column(name = "FROM_DATE", nullable = false)
//    private LocalDate fromDate;
//
//    @Column(name = "TO_DATE", nullable = false)
//    private LocalDate toDate;
//
//}
