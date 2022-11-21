package com.apu.provident.fund.repository.payroll;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface MonthlyPaySlipRepository extends CrudRepository<MonthlyPaySlip, Long> {

    @Query(value="  select payslip " +
            "   from MonthlyPaySlip payslip" +
            "   where ( :employeeId is null or payslip.employee.id = :employeeId ) " +
            "   and ( :fromDate is null or payslip.fromDate >= :fromDate ) " +
            "   and ( :toDate is null or payslip.toDate <= :toDate ) ")
    Page<MonthlyPaySlip> getEmployeeMonthlyPaySlipByDateRangeAndEmployeeId(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("employeeId") Long employeeId,
            Pageable pageable);
}
