package com.igor101.saga.simple;

import com.igor101.saga.simple.controller.OrderController;
import com.igor101.saga.simple.model.*;

import java.util.UUID;

public class NoSagaApp {
    public static void main(String[] args) {
        var shippingService = new ShippingService();
        var paymentService = new PaymentService();

        var orderService = new OrderService(shippingService, paymentService);
        var orderController = new OrderController(orderService);

        var order = new Order(UUID.randomUUID(), "some-order",
                new OrderShipping("New York, center", ShippingMethod.IN_PERSON),
                new OrderPayment(100, PaymentMethod.DIGITAL));


        System.out.println(orderController.create(order));

        var invalidPaymentOrder = new Order(UUID.randomUUID(), "some-order-2",
                new OrderShipping("New York, center", ShippingMethod.IN_PERSON),
                new OrderPayment(-100, PaymentMethod.DIGITAL));

        System.out.println(orderController.create(invalidPaymentOrder));

        var invalidShippingOrder = new Order(UUID.randomUUID(), "some-order-2",
                new OrderShipping("New Yor, center", ShippingMethod.IN_PERSON),
                new OrderPayment(100, PaymentMethod.DIGITAL));

        System.out.println(orderController.create(invalidShippingOrder));

        var fixedShippingOrder = invalidShippingOrder.withShipping(
                new OrderShipping("New York, Suburbs", ShippingMethod.COURIER));

        System.out.println(orderController.update(fixedShippingOrder));
    }
}
