CREATE TABLE todo (
    --sharding key--
    id bigint PRIMARY KEY,
    name text NOT NULL,
    description text
);