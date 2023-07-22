--db size:
--\l+
--indices:
-- \di+ account_not_partitioned*
-- \di+ account_0*
-- timing
-- \timing
-- disable seq scan for tests (for some cases)
SET enable_seqscan = OFF;

select count(*) from account;
explain analyze select * from account where id = '583f305d-b273-4b35-a23f-0eb7405b3a7a' and country_code in (0, 1, 2, 3, 4, 5, 6, 7);
explain analyze select * from account_not_partitioned where id = '583f305d-b273-4b35-a23f-0eb7405b3a7a';

select * from account where name = 'ala' and country_code = 0;
select * from account where name = 'ala';

select * from account_not_partitioned where name = 'ala';

select * from account where description = 'ala';
select * from account where description = 'ala' and country_code in (0,1);
select * from account_not_partitioned where description = 'ala';

select pg_size_pretty(pg_relation_size('account_not_partitioned_pkey'));

select country_code, count(*) from account group by country_code;

SELECT n_live_tup, n_dead_tup, relname FROM
pg_stat_user_tables where relname = 'account_not_partitioned';