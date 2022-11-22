package com.apu.provident.fund.repository.payroll;

import com.apu.provident.fund.entity.payroll.ProvidentFund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface ProvidentFundRepository extends CrudRepository<ProvidentFund, Long> {
    @Query(value="  select pf " +
            "   from ProvidentFund pf" +
            "   where ( :employeeId is null or pf.employeeId = :employeeId )" +
            "   and ( :fromDate is null or pf.fromDate >= :fromDate )" +
            "   and ( :toDate is null or pf.toDate <= :toDate )")
    Page<ProvidentFund> getEmployeeMonthlyPFByDateRangeAndEmployeeId(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("employeeId") Long employeeId,
            Pageable pageable);

    Optional<ProvidentFund> getByEmployeeIdAndFromDateAndToDate(Long employeeId, LocalDate fromDate, LocalDate toDate);
}
