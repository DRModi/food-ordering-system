package com.drmodi.food.ordering.system.order.service.domain;

import com.drmodi.food.ordering.system.order.service.domain.dto.track.TrackOrderQuery;
import com.drmodi.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.drmodi.food.ordering.system.order.service.domain.entity.Order;
import com.drmodi.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import com.drmodi.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.drmodi.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.drmodi.food.ordering.system.order.service.domain.valueobject.TrackingId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
public class OrderTrackCommandHandler {

    private final OrderRepository orderRepository;

    private final OrderDataMapper orderDataMapper;

    public OrderTrackCommandHandler(OrderRepository orderRepository,
                                    OrderDataMapper orderDataMapper) {
        this.orderRepository = orderRepository;
        this.orderDataMapper = orderDataMapper;
    }

    @Transactional(readOnly = true)
    public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery){
        Optional<Order> orderResult = orderRepository.findByTrackingId(new TrackingId(trackOrderQuery.getOrderTrackingId()));

        if(!orderResult.isPresent()){
            log.warn("Could not find order with tracking id; {}", trackOrderQuery.getOrderTrackingId());
            throw new OrderNotFoundException("Could not find order with tracking id: "+
                    trackOrderQuery.getOrderTrackingId());
        }

        return orderDataMapper.orderToTrackOrderResponse(orderResult.get());
    }
}
