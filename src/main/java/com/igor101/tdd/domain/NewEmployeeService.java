package com.igor101.tdd.domain;

import java.util.EnumMap;

/*
This service needs to provide create new employee functionality.

These are requirements:
* it should accept NewEmployee
* it should use NewEmployeeRepository which should be faked in the tests
* it should validate NewEmployee according to the rules described below

NewEmployee validation:
* name can't be null, and it needs to have at least 3 and max 50 characters
* seniority can't be null
* salary needs to be at least 3000 and
    * max 5000 for JUNIOR
    * max 10000 for MID
    * max 20000 for SENIOR
* in all cases of invalid input, meaningful exception should be thrown!

*/
public class NewEmployeeService {

    static final int MIN_NAME_LENGTH = 3;
    static final int MAX_NAME_LENGTH = 50;
    static final int MIN_SALARY = 3000;
    static final int MAX_JUNIOR_SALARY = 5000;
    static final int MAX_MID_SALARY = 10_000;
    static final int MAX_SENIOR_SALARY = 20_000;
    static final EnumMap<Seniority, Integer> MAX_SALARIES_BY_SENIORITY;

    static {
        MAX_SALARIES_BY_SENIORITY = new EnumMap<>(Seniority.class);
        MAX_SALARIES_BY_SENIORITY.put(Seniority.JUNIOR, MAX_JUNIOR_SALARY);
        MAX_SALARIES_BY_SENIORITY.put(Seniority.MID, MAX_MID_SALARY);
        MAX_SALARIES_BY_SENIORITY.put(Seniority.SENIOR, MAX_SENIOR_SALARY);
    }

    private final NewEmployeeRepository repository;

    public NewEmployeeService(NewEmployeeRepository repository) {
        this.repository = repository;
    }

    public void create(NewEmployee employee) {
        validateName(employee.name());
        validateSeniority(employee.seniority());
        validateSalary(employee.seniority(), employee.salary());

        repository.create(employee);
    }

    private void validateName(String name) {
        var nameLength = name == null ? 0 : name.strip().length();
        if (nameLength < MIN_NAME_LENGTH || nameLength > MAX_NAME_LENGTH) {
            throw new NewEmployeeException(
                    "Invalid employee name. It can't be null and it should have between %d and %d characters"
                            .formatted(MIN_NAME_LENGTH, MAX_NAME_LENGTH));
        }
    }

    private void validateSeniority(Seniority seniority) {
        if (seniority == null) {
            throw new NewEmployeeException("Employee seniority can't be null");
        }
    }

    private void validateSalary(Seniority seniority, int salary) {
        boolean invalidSalary = salary < MIN_SALARY;

        if (!invalidSalary) {
            if (seniority == Seniority.JUNIOR && salary > MAX_JUNIOR_SALARY) {
                invalidSalary = true;
            } else if (seniority == Seniority.MID && salary > MAX_MID_SALARY) {
                invalidSalary = true;
            } else if (salary > MAX_SENIOR_SALARY) {
                invalidSalary = true;
            }
        }

        if (invalidSalary) {
            throw new NewEmployeeException("""
                    %d is invalid salary for %s seniority.
                    For given seniority it can be max %d."""
                    .formatted(salary, seniority,
                            MAX_SALARIES_BY_SENIORITY.get(seniority)));
        }
    }
}
