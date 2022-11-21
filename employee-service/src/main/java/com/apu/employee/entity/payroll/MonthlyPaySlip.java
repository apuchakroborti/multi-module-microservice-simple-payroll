//package com.apu.employee.entity.payroll;
//
//import lombok.Getter;
//import lombok.Setter;
//
//import javax.persistence.*;
//import java.time.LocalDate;
//import java.util.List;
//
////using @Getter and @Setter instead of @Data to remove remove recursive toString and EqualsAndHashCode method
//@Entity
//@Getter
//@Setter
//@Table(name = "EMPLOYEE_MONTHLY_PAY_SLIP")
//public class MonthlyPaySlip extends EntityCommon {
//    @Id
//    @GeneratedValue(strategy= GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "EMPLOYEE_ID", nullable = false)
//    private Employee employee;
//
//    @Column(name = "GROSS_SALARY", nullable = false)
//    private Double grossSalary;
//
//    @Column(name = "BASIC_SALARY", nullable = false)
//    private Double basicSalary;
//
//    @Column(name = "HOUSE_RENT")
//    private Double houseRent;
//
//    @Column(name = "CONVEYANCE")
//    private Double conveyanceAllowance;
//
//    @Column(name = "MEDICAL_ALLOWANCE")
//    private Double medicalAllowance;
//
//    private Double due;
//
//    @OneToOne
//    @JoinColumn(name = "PF_DEDUCTION_ID")
//    private ProvidentFund providentFund;
//
//    @OneToMany(mappedBy = "monthlyPaySlip", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<EmployeeTaxDeposit> employeeTaxDepositList;
//
//    @Column(name = "ARREARS")
//    private Double arrears;
//
//    @Column(name = "FESTIVAL_BONUS")
//    private Double festivalBonus;
//
//    @Column(name = "INCENTIVE_BONUS")
//    private Double incentiveBonus;
//
//    @Column(name = "OTHER_PAY")
//    private Double otherPay;
//
//    @Column(name = "NET_PAYMENT")
//    private Double netPayment;
//
//    private String status;
//
//    private String comments;
//
//    @Column(name = "FROM_DATE", nullable = false)
//    private LocalDate fromDate;
//
//    @Column(name = "TO_DATE", nullable = false)
//    private LocalDate toDate;
//}
