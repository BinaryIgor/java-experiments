package com.igor101.saga.simple;

import com.igor101.saga.simple.model.Order;
import com.igor101.saga.simple.model.OrderPayment;
import com.igor101.saga.simple.model.OrderShipping;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class OrderService {

    private final Map<UUID, NoRefsOrder> orders = new HashMap<>();
    private final ShippingService shippingService;
    private final PaymentService paymentService;

    public OrderService(ShippingService shippingService,
                        PaymentService paymentService) {
        this.shippingService = shippingService;
        this.paymentService = paymentService;
    }

    public void create(Order order) {
        orders.put(order.id(), new NoRefsOrder(order.id(), order.name()));

        shippingService.save(order.shipping().toShipping(order.id()));
        paymentService.save(order.payment().toPayment(order.id()));
    }

    public Order ofId(UUID id) {
        return ofIdOpt(id).orElseThrow(() -> new RuntimeException("Order doesn't exist!"));
    }

    public Optional<Order> ofIdOpt(UUID id) {
        return Optional.ofNullable(orders.get(id))
                .map(o -> {
                    var shipping = shippingService.ofId(o.id()).map(OrderShipping::new).orElse(null);
                    var payment = paymentService.ofId(o.id()).map(OrderPayment::new).orElse(null);
                    return new Order(o.id(), o.name(), shipping, payment);
                });
    }

    public void update(Order order) {
        var currentOrder = ofId(order.id());

        var noRefsOrder = orders.get(order.id());
        if (!order.name().equals(currentOrder.name())) {
            orders.put(order.id(), noRefsOrder.withName(order.name()));
        }

        if (!order.shipping().equals(currentOrder.shipping())) {
            shippingService.save(order.shipping().toShipping(order.id()));
        }
        if (!order.payment().equals(currentOrder.payment())) {
            paymentService.save(order.payment().toPayment(order.id()));
        }
    }

    private record NoRefsOrder(UUID id, String name) {

        public NoRefsOrder withName(String name) {
            return new NoRefsOrder(id, name);
        }
    }
}
