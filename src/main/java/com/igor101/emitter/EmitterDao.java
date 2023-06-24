package com.igor101.emitter;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

public class EmitterDao {

    static final Field<String> CATEGORY_PATH = DSL.field("category_path", String.class);
    static final Field<BigDecimal> MAX_CPC = DSL.field("max_cpc", BigDecimal.class);
    static final Field<String> SKU = DSL.field("sku", String.class);
    static final Field<UUID> CAMPAIGN_ID = DSL.field("campaign_id", UUID.class);
    static final Field<UUID> ACCOUNT_ID = DSL.field("account_id", UUID.class);
    static final Field<String> TITLE = DSL.field("title", String.class);
    static final Field<BigDecimal> PRICE = DSL.field("price", BigDecimal.class);
    static final Field<Boolean> SPECIAL_OFFER = DSL.field("special_offer", Boolean.class);
    static final Field<String> BRAND = DSL.field("brand", String.class);
    static final Table<?> CATEGORY_PATH_TABLE = DSL.table("category_path");
    static final Table<?> SPONSORED_PRODUCT_TABLE = DSL.table("sponsored_product");
    static final Table<?> BRAND_TABLE = DSL.table("brand");
    private static final int BATCH_SIZE = 100;
    private final Semaphore semaphore = new Semaphore(10);
    private final DSLContext context;

    public EmitterDao(DSLContext context) {
        this.context = context;
    }

    public void insertSponsoredProducts(List<SponsoredProduct> sponsoredProducts) {
        insertInBuckets(sponsoredProducts, bucket -> {
            var insert = context.insertInto(SPONSORED_PRODUCT_TABLE)
                    .columns(SKU, CAMPAIGN_ID, ACCOUNT_ID, TITLE, MAX_CPC, PRICE, SPECIAL_OFFER);
            bucket.forEach(
                    v -> insert.values(v.sku(), v.campaignId(), v.accountId(), v.title(), v.maxCpc(),
                            v.price(), v.specialOffer()));
            insert.execute();
        });
    }

    private <T> void insertInBuckets(List<T> data, Consumer<List<T>> insert) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var idx = 0;
            while (true) {
                var bucket = nextBucket(data, idx);
                if (bucket.isEmpty()) {
                    break;
                }

                executor.submit(() -> {
                    try {
                        semaphore.acquire();
                        insert.accept(bucket);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        semaphore.release();
                    }
                });

                idx++;
            }
        }
    }

    private <T> List<T> nextBucket(List<T> data, int index) {
        return data.stream().skip((long) index * BATCH_SIZE).limit(BATCH_SIZE).toList();
    }

    public void insertCategoryPaths(List<CategoryPath> categoryPaths) {
        insertInBuckets(categoryPaths, bucket -> {
            var insert = context.insertInto(CATEGORY_PATH_TABLE)
                    .columns(CATEGORY_PATH, MAX_CPC, SKU);
            bucket.forEach(v -> insert.values(v.categoryPath(), v.maxCpc(), v.sku()));
            insert.execute();
        });
    }

    public void insertBrands(List<Brand> brands) {
        insertInBuckets(brands, bucket -> {
            var insert = context.insertInto(BRAND_TABLE)
                    .columns(BRAND, MAX_CPC, SKU);
            bucket.forEach(v -> insert.values(v.brand(), v.maxCpc(), v.sku()));
            insert.execute();
        });
    }
}
