package com.drmodi.food.ordering.system.order.service.domain.ports.output.message.publisher.payment;

import com.drmodi.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.drmodi.food.ordering.system.order.service.domain.event.OrderCancelledEvent;


public interface OrderCancelledPaymentRequestMessagePublisher extends DomainEventPublisher<OrderCancelledEvent> {
}
