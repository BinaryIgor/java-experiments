package com.igor101.emitter;

import java.math.BigDecimal;
import java.util.UUID;

public record SponsoredProduct(String sku,
                               UUID campaignId,
                               UUID accountId,
                               String title,
                               BigDecimal maxCpc,
                               BigDecimal price,
                               boolean specialOffer) {
}
