package com.apu.employee.repository.payroll;

import com.apu.employee.entity.payroll.EmployeeSalary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface EmployeeSalaryRepository extends CrudRepository<EmployeeSalary, Long> {

    @Query(value="  select salary " +
            "   from EmployeeSalary salary " +
            "   where ( :employeeId is null or salary.employee.id = :employeeId ) " +
            "   and (:fromDate is null or salary.fromDate >= :fromDate) " +
            "   and (:toDate is null or salary.toDate <= :toDate )")
    Page<EmployeeSalary> getEmployeeSalaryByDateRangeAndEmployeeId(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("employeeId") Long employeeId,
            Pageable pageable);

    @Query(value="  select salary " +
            "   from EmployeeSalary salary " +
            "   where salary.employee.id = :employeeId " +
            "   and salary.toDate is null")
    EmployeeSalary getEmployeeCurrentSalaryByEmployeeId(
            @Param("employeeId") Long employeeId);

}
