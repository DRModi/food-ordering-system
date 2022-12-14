package com.drmodi.food.ordering.system.order.service.domain;

import com.drmodi.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.drmodi.food.ordering.system.order.service.domain.entity.Customer;
import com.drmodi.food.ordering.system.order.service.domain.entity.Order;
import com.drmodi.food.ordering.system.order.service.domain.entity.Restaurant;
import com.drmodi.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.drmodi.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.drmodi.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.drmodi.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.drmodi.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.drmodi.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OrderCreateHelper {

    private final OrderDomainService orderDomainService;

    private final OrderRepository orderRepository;

    private final CustomerRepository customerRepository;

    private final RestaurantRepository restaurantRepository;

    private final OrderDataMapper orderDataMapper;

    public OrderCreateHelper(OrderDomainService orderDomainService,
                             OrderRepository orderRepository,
                             CustomerRepository customerRepository,
                             RestaurantRepository restaurantRepository,
                             OrderDataMapper orderDataMapper) {
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderDataMapper = orderDataMapper;
    }


    @Transactional
    public OrderCreatedEvent persistOrder(CreateOrderCommand createOrderCommand){
        checkCustomer(createOrderCommand.getCustomerId());
        Restaurant restaurant = checkRestaurant(createOrderCommand);
        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order, restaurant);
        Order orderResult = saveOrder(order);
        log.info("Order is created with id: {}", orderResult.getId().getValue());
        return orderCreatedEvent;
    }

    private Restaurant checkRestaurant(CreateOrderCommand createOrderCommand) {
        Restaurant restaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
        Optional<Restaurant> optionalRestaurantInformation = restaurantRepository.findRestaurantInformation(restaurant);

        if(!optionalRestaurantInformation.isPresent()){
            log.warn("Could not find restaurant with restaurant id: {}", createOrderCommand.getRestaurantId());
            throw new OrderDomainException("Could not find restaurant with restaurant id: "+createOrderCommand.getCustomerId());
        }


        return optionalRestaurantInformation.get();

    }

    private void checkCustomer(UUID customerId) {
        Optional<Customer> customer = customerRepository.findCustomer(customerId);
        if (customer.isPresent()) {
            return;
        }
        log.warn("Could not find customer with customer id: {}", customerId);
        throw new OrderDomainException("Could not find customer with id: "+customer);
    }

    private Order saveOrder(Order order){
        Order orderResult = orderRepository.save(order);
        if(orderResult == null){
            log.error("Could not save order!");
            throw new OrderDomainException("Could not save order!");
        }
        log.info("Order is saved with id: {}", orderResult.getId().getValue());
        return orderResult;
    }


}
