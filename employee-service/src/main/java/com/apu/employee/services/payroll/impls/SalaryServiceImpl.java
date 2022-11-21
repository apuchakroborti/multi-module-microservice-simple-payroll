package com.apu.employee.services.payroll.impls;

import com.apu.employee.dto.EmployeeSalaryDto;
import com.apu.employee.dto.request.SalarySearchCriteria;
import com.apu.employee.dto.response.ServiceResponse;
import com.apu.employee.exceptions.EmployeeNotFoundException;
import com.apu.employee.exceptions.GenericException;
import com.apu.employee.entity.payroll.Employee;
import com.apu.employee.entity.payroll.EmployeeSalary;
import com.apu.employee.repository.payroll.EmployeeRepository;
import com.apu.employee.repository.payroll.EmployeeSalaryRepository;
//import com.apu.employee.services.payroll.MonthlyPaySlipService;
import com.apu.employee.services.payroll.SalaryService;
import com.apu.employee.utils.Defs;
import com.apu.employee.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;


@Service
public class SalaryServiceImpl implements SalaryService {
    Logger logger = LoggerFactory.getLogger(SalaryServiceImpl.class);

    @Autowired
    EmployeeSalaryRepository employeeSalaryRepository;
    @Autowired
    EmployeeRepository employeeRepository;

    /*@Autowired
    MonthlyPaySlipService monthlyPaySlipService;*/

    @Override
    public EmployeeSalaryDto updateSalaryData(EmployeeSalaryDto employeeSalaryDto) throws GenericException {
        try {
            //TODO check fromDate must be after the month of joining date
            EmployeeSalary employeeSalary = employeeSalaryRepository.getEmployeeCurrentSalaryByEmployeeId(employeeSalaryDto.getEmployee().getId());
            Employee employee = null;
            if (employeeSalary != null) {
                employeeSalary.setToDate(employeeSalaryDto.getFromDate().minusDays(1));
                employeeSalary.setStatus(false);
                employee = employeeSalary.getEmployee();
                employeeSalaryRepository.save(employeeSalary);
            } else {
                Optional<Employee> optionalEmployee = employeeRepository.findById(employeeSalaryDto.getEmployee().getId());
                if (!optionalEmployee.isPresent()) {
                    throw new EmployeeNotFoundException(Defs.EMPLOYEE_NOT_FOUND);
                }
                employeeSalaryDto.setFromDate(optionalEmployee.get().getDateOfJoining());
                if (employee == null) employee = optionalEmployee.get();
            }
            EmployeeSalary employeeSalaryNew = new EmployeeSalary();
            Utils.copyProperty(employeeSalaryDto, employeeSalaryNew);

            Double basic = employeeSalaryDto.getGrossSalary() * 60 / 100.0;
            employeeSalaryNew.setBasicSalary(basic);
            employeeSalaryNew.setStatus(true);

            employeeSalaryNew.setCreatedBy(1L);
            employeeSalaryNew.setCreateTime(LocalDateTime.now());
            employeeSalaryNew.setEmployee(employee);
            employeeSalaryNew = employeeSalaryRepository.save(employeeSalaryNew);
            Utils.copyProperty(employeeSalaryNew, employeeSalaryDto);

            //TODO microservice call needed
            //update payslip's info from this month to last month for this financial year
//            monthlyPaySlipService.generatePayslipForCurrentFinancialYear(employee, employeeSalaryNew, employeeSalaryDto.getFromDate());

            return employeeSalaryDto;
        }catch (Exception e){
            logger.error("Error occurred while update salary by employee id: {}", employeeSalaryDto.getEmployee().getId());
            throw new GenericException(e.getMessage(), e);
        }
    }
    @Override
    public Page<EmployeeSalary> getSalaryDataWithInDateRangeAndEmployeeId(SalarySearchCriteria searchCriteria, Pageable pageable)throws GenericException{
        try {
            Page<EmployeeSalary> employeeSalaryPage = employeeSalaryRepository.getEmployeeSalaryByDateRangeAndEmployeeId(
                    searchCriteria.getFromDate(),
                    searchCriteria.getToDate(),
                    searchCriteria.getEmployeeId(),
                    pageable);
            return employeeSalaryPage;
        }catch (Exception e){
            logger.error("Error occurred while fetching salary");
            throw new GenericException("Internal server error", e);
        }
    }
    @Override
    public EmployeeSalaryDto getCurrentSalaryByEmployeeId(Long employeeId) throws GenericException {
        try {

            EmployeeSalary employeeCurrentSalary = employeeSalaryRepository.getEmployeeCurrentSalaryByEmployeeId(employeeId);
            EmployeeSalaryDto employeeSalaryDto = new EmployeeSalaryDto();
            Utils.copyProperty(employeeCurrentSalary, employeeSalaryDto);

            return employeeSalaryDto;
        }catch (Exception e){
            logger.error("Error occurred while fetching current salary of an employee id: {}", employeeId);
            throw new GenericException(e.getMessage(), e);
        }
    }
}
