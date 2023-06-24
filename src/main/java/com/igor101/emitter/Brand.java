package com.igor101.emitter;

import java.math.BigDecimal;

public record Brand(String brand,
                    BigDecimal maxCpc,
                    String sku) {
}
