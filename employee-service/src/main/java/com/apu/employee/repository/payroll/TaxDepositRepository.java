//package com.apu.employee.repository.payroll;
//
//import com.apu.employee.entity.payroll.EmployeeTaxDeposit;
//import com.apu.employee.utils.TaxType;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.CrudRepository;
//import org.springframework.data.repository.query.Param;
//
//import java.time.LocalDate;
//import java.util.Optional;
//
//public interface TaxDepositRepository extends CrudRepository<EmployeeTaxDeposit, Long> {
//    Page<EmployeeTaxDeposit> findAllByEmployeeId(Long employeeId, Pageable pageable);
//
//    @Query(value="  select tax " +
//            "   from EmployeeTaxDeposit tax" +
//            "   where (:employeeId is null or tax.employee.id = :employeeId ) " +
//            "   and ( :fromDate is null or tax.fromDate >= :fromDate ) " +
//            "   and ( :toDate is null or tax.toDate <= :toDate ) ")
//    Page<EmployeeTaxDeposit> getAllByEmployeeIdAndFromDateAndToDate(
//            @Param("fromDate") LocalDate fromDate,
//            @Param("toDate") LocalDate toDate,
//            @Param("employeeId") Long employeeId,
//            Pageable pageable
//            );
//    Optional<EmployeeTaxDeposit> findByEmployeeIdAndFromDateAndToDateAndTaxType(Long employeeId, LocalDate fromDate, LocalDate toDate, TaxType taxType);
//}
