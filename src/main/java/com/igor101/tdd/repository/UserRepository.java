package com.igor101.tdd.repository;

import java.util.Optional;

/*
We need to create a SqlUserRepository that implements UserRepository.
It needs to get data from the Postgres database.
We need to use real database in our tests.
*/
public interface UserRepository {
    Optional<User> ofId(long id);
}
