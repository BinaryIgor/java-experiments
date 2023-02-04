package com.igor101.saga.simple;

import com.igor101.saga.simple.model.Shipping;

import java.util.*;

public class ShippingService {

    private static final List<String> SUPPORTED_CITIES = List.of("New York", "Los Angeles");
    private final Map<UUID, Shipping> shippings = new HashMap<>();

    public void save(Shipping shipping) {
        validateShipping(shipping);
        shippings.put(shipping.id(), shipping);
    }

    private void validateShipping(Shipping shipping) {
        for (var c : SUPPORTED_CITIES) {
            if (shipping.address().contains(c)) {
                return;
            }
        }

        throw new RuntimeException("Given address is not supported!");
    }

    public Optional<Shipping> ofId(UUID id) {
        return Optional.ofNullable(shippings.get(id));
    }
}
