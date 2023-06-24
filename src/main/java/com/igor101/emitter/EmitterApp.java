package com.igor101.emitter;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EmitterApp {

    private static final int PRODUCTS = 1_000_000;
    private static final int MAX_SKU = 100_000_000;
    private static final Random RANDOM = new SecureRandom();
    private static final Map<String, List<String>> CATEGORY_PATHS_TO_EXPLODED_CATEGORY_PATHS =
            Map.of("a>b>c", List.of("a", "a>b", "a>b>c"),
                    "d>e>f>g", List.of("d", "d>e", "d>e>f", "d>e>f>g"),
                    "a>y>z>x", List.of("a", "a>y", "a>y>z", "a>y>z>x"),
                    "a>1>c>d", List.of("a", "a>1", "a>1>c", "a>1>c>d"));
    private static final List<String> CATEGORY_PATH_KEYS = new ArrayList<>(
            CATEGORY_PATHS_TO_EXPLODED_CATEGORY_PATHS.keySet());

    private static final List<String> BRANDS = List.of("a", "b", "c", "d", "e", "f", "g", "h");

    public static void main(String[] args) {
        var context = dbContext();
        var dao = new EmitterDao(context);

        var products = generateSponsoredProducts(PRODUCTS);
        var categoryPaths = generateCategoryPaths(products);
        var brands = generateBrands(products);

        dao.insertSponsoredProducts(products);
        dao.insertCategoryPaths(categoryPaths);
        dao.insertBrands(brands);
    }

    private static DSLContext dbContext() {
        var config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5555/postgres");
        config.setUsername("postgres");
        config.setPassword("postgres");
        config.setMinimumIdle(10);
        config.setMaximumPoolSize(10);

        return DSL.using(new DefaultConfiguration()
                .set(new HikariDataSource(config))
                .set(SQLDialect.POSTGRES));
    }

    private static List<SponsoredProduct> generateSponsoredProducts(int size) {
        return Stream.generate(EmitterApp::generateSponsoredProductInVersions)
                .flatMap(List::stream)
                .limit(size)
                .toList();
    }

    private static List<SponsoredProduct> generateSponsoredProductInVersions() {
        var sku = String.valueOf(1 + RANDOM.nextInt(MAX_SKU));
        var title = UUID.randomUUID().toString();

        return Stream.generate(() -> new SponsoredProduct(sku,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        title, generateMaxCpc(), generatePrice(), RANDOM.nextBoolean()))
                .limit(1 + RANDOM.nextInt(10))
                .toList();
    }

    private static BigDecimal generateMaxCpc() {
        return BigDecimal.valueOf(100 + RANDOM.nextInt(10_000), 2);
    }

    private static BigDecimal generatePrice() {
        return BigDecimal.valueOf(100 + RANDOM.nextInt(10_000), 2);
    }

    private static List<CategoryPath> generateCategoryPaths(List<SponsoredProduct> sponsoredProducts) {
        var groupedProducts = sponsoredProducts.stream()
                .collect(Collectors.groupingBy(SponsoredProduct::sku));

        return groupedProducts.entrySet().stream()
                .flatMap(e -> {
                    var sku = e.getKey();
                    var cpValue = nextCategoryPathKey();

                    var maxCpcOfProducts = e.getValue().stream()
                            .map(SponsoredProduct::maxCpc)
                            .max(Comparator.naturalOrder())
                            .orElseThrow();

                    return CATEGORY_PATHS_TO_EXPLODED_CATEGORY_PATHS.get(cpValue).stream()
                            .map(c -> new CategoryPath(c, maxCpcOfProducts, sku));
                })
                .toList();
    }

    private static String nextCategoryPathKey() {
        var idx = RANDOM.nextInt(CATEGORY_PATH_KEYS.size());
        return CATEGORY_PATH_KEYS.get(idx);
    }

    private static List<Brand> generateBrands(List<SponsoredProduct> sponsoredProducts) {
        var groupedProducts = sponsoredProducts.stream()
                .collect(Collectors.groupingBy(SponsoredProduct::sku));

        return groupedProducts.entrySet().stream()
                .map(e -> {
                    var sku = e.getKey();
                    var brand = nextBrand();

                    var maxCpcOfProducts = e.getValue().stream()
                            .map(SponsoredProduct::maxCpc)
                            .max(Comparator.naturalOrder())
                            .orElseThrow();

                    return new Brand(brand, maxCpcOfProducts, sku);
                })
                .toList();
    }

    private static String nextBrand() {
        var idx = RANDOM.nextInt(BRANDS.size());
        return BRANDS.get(idx);
    }
}
