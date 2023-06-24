package com.igor101.emitter;

import java.math.BigDecimal;

public record CategoryPath(String categoryPath,
                           BigDecimal maxCpc,
                           String sku) {
}
