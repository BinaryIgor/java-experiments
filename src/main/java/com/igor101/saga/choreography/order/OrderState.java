package com.igor101.saga.choreography.order;

enum OrderState {
    CREATED, SHIPPING_REJECTED, SHIPPING_ACCEPTED, PAYMENT_REJECTED, PAID
}
