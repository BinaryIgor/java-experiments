package com.igor101.saga.simple.controller;

import com.igor101.saga.simple.OrderService;
import com.igor101.saga.simple.model.Order;

import java.util.UUID;

public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    public OrderMutationResponse create(Order order) {
        try {
            service.create(order);
            return OrderMutationResponse.success(order);
        } catch (Exception e) {
            return OrderMutationResponse.failure(service.ofIdOpt(order.id()).orElse(null), e.getMessage());
        }
    }

    public OrderMutationResponse update(Order order) {
        try {
            service.update(order);
            return OrderMutationResponse.success(order);
        } catch (Exception e) {
            return OrderMutationResponse.failure(service.ofId(order.id()), e.getMessage());
        }
    }

    public Order ofId(UUID id) {
        return service.ofId(id);
    }
}
