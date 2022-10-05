package com.igor101.tdd.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NewEmployeeServiceTest {

    private NewEmployeeService service;
    private FakeNewEmployeeRepository repository;

    @BeforeEach
    void setup() {
        repository = new FakeNewEmployeeRepository();
        service = new NewEmployeeService(repository);
    }

    @ParameterizedTest
    @MethodSource("invalidEmployeeNames")
    void create_givenEmployeeWithInvalidName_shouldThrowException(String invalidName) {
        var invalidEmployee = new NewEmployee(invalidName, Seniority.MID, 4000);

        var thrownException = Assertions.assertThrows(NewEmployeeException.class,
                () -> service.create(invalidEmployee));

        Assertions.assertEquals(
                "Invalid employee name. It can't be null and it should have between 3 and 50 characters",
                thrownException.getMessage());
    }

    @Test
    void create_givenEmployeeWithNullSeniority_shouldThrowException() {
        var invalidEmployee = new NewEmployee("some-name", null, 4000);

        var thrownException = Assertions.assertThrows(NewEmployeeException.class,
                () -> service.create(invalidEmployee));

        Assertions.assertEquals("Employee seniority can't be null", thrownException.getMessage());
    }

    @ParameterizedTest
    @MethodSource("employeesWithInvalidSalaries")
    void create_givenEmployeeWithInvalidSalary_shouldThrowException(NewEmployee invalidEmployee) {
        var thrownException = Assertions.assertThrows(NewEmployeeException.class,
                () -> service.create(invalidEmployee));

        var maxSalary = NewEmployeeService.MAX_SALARIES_BY_SENIORITY.get(invalidEmployee.seniority());

        Assertions.assertEquals("""
                        %d is invalid salary for %s seniority.
                        For given seniority it can be max %d."""
                        .formatted(invalidEmployee.salary(), invalidEmployee.seniority(), maxSalary),
                thrownException.getMessage());
    }

    @ParameterizedTest
    @MethodSource("validEmployees")
    void create_givenValidEmployee_shouldCreateIt(NewEmployee validEmployee) {
        Assertions.assertNull(repository.lastEmployee());

        service.create(validEmployee);

        Assertions.assertEquals(validEmployee, repository.lastEmployee());
    }

    static Stream<String> invalidEmployeeNames() {
        var tooLongName = Stream.generate(() -> "d")
                .limit(NewEmployeeService.MAX_NAME_LENGTH + 1)
                .collect(Collectors.joining());

        return Stream.of(null, "", " ", "ad", tooLongName);
    }

    static Stream<NewEmployee> employeesWithInvalidSalaries() {
        var name = "some-name";
        return Stream.of(new NewEmployee(name, Seniority.JUNIOR, -1),
                new NewEmployee(name, Seniority.JUNIOR, NewEmployeeService.MIN_SALARY - 1),
                new NewEmployee(name, Seniority.JUNIOR, NewEmployeeService.MAX_JUNIOR_SALARY + 1),
                new NewEmployee(name, Seniority.MID, 100),
                new NewEmployee(name, Seniority.MID, NewEmployeeService.MIN_SALARY - 1),
                new NewEmployee(name, Seniority.MID, NewEmployeeService.MAX_MID_SALARY + 1),
                new NewEmployee(name, Seniority.SENIOR, 0),
                new NewEmployee(name, Seniority.SENIOR, NewEmployeeService.MIN_SALARY - 1),
                new NewEmployee(name, Seniority.SENIOR, NewEmployeeService.MAX_SENIOR_SALARY + 1));
    }

    static Stream<NewEmployee> validEmployees() {
        return Stream.of(
                new NewEmployee("junior-name1", Seniority.JUNIOR, NewEmployeeService.MIN_SALARY),
                new NewEmployee("junior-name2", Seniority.JUNIOR, 4000),
                new NewEmployee("junior-name3", Seniority.JUNIOR, NewEmployeeService.MAX_JUNIOR_SALARY),
                new NewEmployee("mid-name1", Seniority.MID, NewEmployeeService.MIN_SALARY),
                new NewEmployee("mid-name2", Seniority.MID, 6_000),
                new NewEmployee("mid-name3", Seniority.MID, NewEmployeeService.MAX_MID_SALARY),
                new NewEmployee("senior-name1", Seniority.SENIOR, NewEmployeeService.MIN_SALARY),
                new NewEmployee("senior-name2", Seniority.SENIOR, 12_000),
                new NewEmployee("senior-name3", Seniority.SENIOR, NewEmployeeService.MAX_SENIOR_SALARY));
    }
}
