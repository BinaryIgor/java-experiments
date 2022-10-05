package com.igor101.tdd.domain;

public class FakeNewEmployeeRepository implements NewEmployeeRepository {

    private NewEmployee lastEmployee;

    @Override
    public void create(NewEmployee employee) {
        lastEmployee = employee;
    }

    public NewEmployee lastEmployee() {
        return lastEmployee;
    }
}
