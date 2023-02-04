package com.igor101.saga.simple.controller;

import com.igor101.saga.simple.model.Order;

public record OrderMutationResponse(Order order,
                                    String exception) {

    public static OrderMutationResponse success(Order order) {
        return new OrderMutationResponse(order, null);
    }

    public static OrderMutationResponse failure(Order order, String exception) {
        return new OrderMutationResponse(order, exception);
    }
}
