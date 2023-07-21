CREATE TABLE account (
    id UUID NOT NULL,
    name TEXT NOT NULL,
    country_code INTEGER NOT NULL,
    description TEXT,

    PRIMARY KEY(country_code, id)
) PARTITION BY LIST(country_code);

CREATE INDEX account_name ON account(name);

CREATE TABLE account_not_partitioned (
    id UUID NOT NULL,
    name TEXT NOT NULL,
    country_code INTEGER NOT NULL,
    description TEXT,

    PRIMARY KEY(id)
);

CREATE INDEX account_not_partitioned_name ON account_not_partitioned(name);