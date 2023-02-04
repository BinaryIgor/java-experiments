package com.igor101.saga.choreography;

import com.igor101.events.InMemoryEvents;
import com.igor101.saga.choreography.order.CreateOrder;
import com.igor101.saga.choreography.order.Order;
import com.igor101.saga.choreography.order.OrderService;
import com.igor101.saga.choreography.payment.PaymentService;
import com.igor101.saga.choreography.shipping.Shipping;
import com.igor101.saga.choreography.shipping.ShippingMethod;
import com.igor101.saga.choreography.shipping.ShippingService;

public class SagaChoreographyApp {
    public static void main(String[] args) {
        var events = new InMemoryEvents();
        var eventPublisher = events.publisher();

        var orderService = new OrderService(events);
        var shippingService = new ShippingService(events);
        var paymentService = new PaymentService(events);

        var validOrder = Order.newOrder("some-toys",
                new Shipping("New York, center", ShippingMethod.IN_PERSON),
                10);
        var invalidOrder = Order.newOrder("too-expensive", new Shipping("Los Angeles, suburbs", ShippingMethod.COURIER),
                999999);

        eventPublisher.publish(new CreateOrder(validOrder));

        System.out.println(orderService.orderOfId(validOrder.id()));

        eventPublisher.publish(new CreateOrder(invalidOrder));

        System.out.println(orderService.orderOfId(invalidOrder.id()));
    }
}
