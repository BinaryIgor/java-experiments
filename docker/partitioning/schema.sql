-- What is table partitioning?
-- It is splitting your database table to multiple sub-tables (partitions),
-- so that we can reduce the search space and make our queries faster.

CREATE TABLE account (
    id UUID NOT NULL,
    name TEXT NOT NULL,
    country_code INTEGER NOT NULL,
    description TEXT,

    PRIMARY KEY(country_code, id)
) PARTITION BY LIST(country_code);

--LIST
--country_code=0 -> account_0
--country_code=1 -> account_1
--country_code=2 -> account_2

--RANGE
--country_code 0-10 -> account_0
--country_code 10-20 -> account_1
--country_code 20-30 -> account_2

--HASH
--hash value of country_code % 3 = 0 -> account_0
--hash value of country_code % 3 = 1 -> account_1
--hash value of country_code % 3 = 2 -> account_2

CREATE INDEX account_name ON account(name);

CREATE TABLE account_not_partitioned (
    id UUID NOT NULL,
    name TEXT NOT NULL,
    country_code INTEGER NOT NULL,
    description TEXT,

    PRIMARY KEY(id)
);

CREATE INDEX account_not_partitioned_name ON account_not_partitioned(name);