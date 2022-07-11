package com.drmodi.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval;

import com.drmodi.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.drmodi.food.ordering.system.order.service.domain.event.OrderPaidEvent;

public interface OrderPaidRestaurantMessagePublisher extends DomainEventPublisher<OrderPaidEvent> {
}
