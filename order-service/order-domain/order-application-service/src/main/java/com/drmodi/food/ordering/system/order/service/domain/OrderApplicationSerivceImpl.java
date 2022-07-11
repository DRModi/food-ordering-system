package com.drmodi.food.ordering.system.order.service.domain;

import com.drmodi.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.drmodi.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.drmodi.food.ordering.system.order.service.domain.dto.track.TrackOrderQuery;
import com.drmodi.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.drmodi.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;

import javax.validation.Valid;

class OrderApplicationSerivceImpl implements OrderApplicationService {

    private final OrderCreateCommandHandler orderCreateCommandHandler;
    private final OrderTrackCommandHandler orderTrackCommandHandler;

    public OrderApplicationSerivceImpl(OrderCreateCommandHandler orderCreateCommandHandler, OrderTrackCommandHandler orderTrackCommandHandler) {
        this.orderCreateCommandHandler = orderCreateCommandHandler;
        this.orderTrackCommandHandler = orderTrackCommandHandler;
    }

    @Override
    public CreateOrderResponse createOrder(@Valid CreateOrderCommand createOrderCommand) {
        return orderCreateCommandHandler.createOrder(createOrderCommand);
    }

    @Override
    public TrackOrderResponse trackOrder(@Valid TrackOrderQuery trackOrderQuery) {
        return orderTrackCommandHandler.trackOrder(trackOrderQuery);
    }
}
