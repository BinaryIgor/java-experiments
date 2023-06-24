CREATE TABLE category_path (
    category_path TEXT NOT NULL,
    max_cpc NUMERIC(6, 2) NOT NULL,
    sku TEXT NOT NULL
);
CREATE UNIQUE INDEX category_path_idx ON category_path(category_path, max_cpc DESC, sku);

CREATE TABLE brand (
    brand TEXT NOT NULL,
    max_cpc NUMERIC(6, 2) NOT NULL,
    sku TEXT NOT NULL
);
CREATE UNIQUE INDEX brand_idx ON brand(brand, max_cpc DESC, sku);

CREATE TABLE sponsored_product (
    sku TEXT NOT NULL,
    campaign_id UUID NOT NULL,
    account_id UUID NOT NULL,
    title TEXT NOT NULL,
    max_cpc NUMERIC(6, 2) NOT NULL,
    price NUMERIC(6, 2) NOT NULL,
    special_offer boolean NOT NULL
);
CREATE INDEX sponsored_product_sku_max_cpc ON sponsored_product(sku, max_cpc);

--SELECT DISTINCT ON(sku) sku.* FROM sponsored_product
--WHERE sku IN
--(SELECT DISTINCT sku FROM category_path
--WHERE category_path = ?
--ORDER BY category_path, max_cpc DESC
--LIMIT ?)
--ORDER BY sku, max_cpc
--LIMIT ?
--
SELECT DISTINCT ON(sku) sp.* FROM sponsored_product sp
WHERE sku IN
(SELECT sku FROM category_path cp
WHERE category_path = 'a'
ORDER BY cp.category_path, cp.max_cpc DESC, cp.sku
LIMIT 100)
ORDER BY sku, max_cpc
LIMIT 100;

SELECT DISTINCT ON(sku) sp.* FROM sponsored_product sp
WHERE sku IN
(SELECT sku FROM category_path cp
WHERE category_path = 'a>b'
ORDER BY cp.category_path, cp.max_cpc DESC, cp.sku
LIMIT 1000)
AND price > '50.00' AND special_offer = true
ORDER BY sku, max_cpc
LIMIT 100;

--EXPLAIN ANALYZE SELECT DISTINCT ON(sku) sp.* FROM
--    (SELECT sku FROM category_path cp
--    WHERE category_path = 'a'
--    ORDER BY cp.category_path, cp.max_cpc DESC, cp.sku
--    LIMIT 100) cp
--INNER JOIN sponsored_product sp ON cp.sku = sp.sku
--ORDER BY sku, max_cpc
--LIMIT 100;

SELECT DISTINCT ON(sku) sp.* FROM sponsored_product sp
WHERE sku IN
(SELECT sku FROM brand
WHERE brand = 'b'
ORDER BY brand, max_cpc DESC, sku
LIMIT 100)
ORDER BY sku, max_cpc
LIMIT 100;

EXPLAIN ANALYZE SELECT DISTINCT ON(sku) sp.* FROM sponsored_product sp
WHERE sku IN
(SELECT sku FROM brand
WHERE brand = 'b'
ORDER BY brand, max_cpc DESC, sku
LIMIT 1000)
AND special_offer = true
ORDER BY sku, max_cpc
LIMIT 100;