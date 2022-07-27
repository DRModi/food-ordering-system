package com.drmodi.food.ordering.system.order.service.domain;

import com.drmodi.food.ordering.system.domain.valueobject.*;
import com.drmodi.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.drmodi.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.drmodi.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.drmodi.food.ordering.system.order.service.domain.dto.create.OrderItem;
import com.drmodi.food.ordering.system.order.service.domain.entity.Customer;
import com.drmodi.food.ordering.system.order.service.domain.entity.Order;
import com.drmodi.food.ordering.system.order.service.domain.entity.Product;
import com.drmodi.food.ordering.system.order.service.domain.entity.Restaurant;
import com.drmodi.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.drmodi.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.drmodi.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import com.drmodi.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.drmodi.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.drmodi.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.List.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderApplicationServiceTest {

    @Autowired
    private OrderApplicationService orderApplicationService;

    @Autowired
    private OrderDataMapper orderDataMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CustomerRepository customerRepository;


    private CreateOrderCommand createOrderCommand;
    private CreateOrderCommand createOrderCommandWrongPrice;
    private CreateOrderCommand createOrderCommandWrongProductPrice;

    private final UUID CUSTOMER_ID = UUID.fromString("123e4567-e89b-12d3-a456-556642440000");
    private final UUID RESTAURANT_ID = UUID.fromString("123e4567-e89b-12d3-a456-556642440000");
    private final UUID PRODUCT_ID = UUID.fromString("123e4567-e89b-12d3-a456-556642440000");
    private final UUID ORDER_ID = UUID.fromString("213e4567-e89b-12d3-a456-556642440000");
    private final BigDecimal PRICE = new BigDecimal("200.00");

    @BeforeAll
    public void init(){

        List<OrderItem> itemsList = new java.util.ArrayList<>();
        itemsList.add(OrderItem.builder()
                .productId(PRODUCT_ID)
                .quantity(1)
                .price(new BigDecimal("50.00"))
                .subTotal(new BigDecimal("50.00"))
                .build());
        itemsList.add(OrderItem.builder()
                .productId(PRODUCT_ID)
                .quantity(3)
                .price(new BigDecimal("50.00"))
                .subTotal(new BigDecimal("150.00"))
                .build());


        createOrderCommand = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("streetLine1")
                        .postalCode("38400GJ")
                        .city("CityTest")
                        .build())
                .price(PRICE)
                .items(itemsList)
                .build();


        List<OrderItem> itemsListForWrongPrice = new java.util.ArrayList<>();
        itemsListForWrongPrice.add(OrderItem.builder()
                        .productId(PRODUCT_ID)
                        .quantity(1)
                        .price(new BigDecimal("50.00"))
                        .subTotal(new BigDecimal("50.00"))
                        .build());
        itemsListForWrongPrice.add(OrderItem.builder()
                        .productId(PRODUCT_ID)
                        .quantity(3)
                        .price(new BigDecimal("50.00"))
                        .subTotal(new BigDecimal("150.00"))
                        .build());


        createOrderCommandWrongPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("streetLine1")
                        .postalCode("38400GJ")
                        .city("CityTest")
                        .build())
                .price(new BigDecimal("250.00"))
                .items(itemsListForWrongPrice)
                .build();




        List<OrderItem> itemsListForWrongProductPrice = new java.util.ArrayList<>();
        itemsListForWrongProductPrice.add(OrderItem.builder()
                    .productId(PRODUCT_ID)
                    .quantity(1)
                    .price(new BigDecimal("60.00"))
                    .subTotal(new BigDecimal("60.00"))
                    .build());
        itemsListForWrongProductPrice.add(OrderItem.builder()
                    .productId(PRODUCT_ID)
                    .quantity(3)
                    .price(new BigDecimal("50.00"))
                    .subTotal(new BigDecimal("150.00"))
                    .build());


        createOrderCommandWrongProductPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("streetLine1")
                        .postalCode("38400GJ")
                        .city("CityTest")
                        .build())
                .price(new BigDecimal("210.00"))
                .items(itemsListForWrongProductPrice)
                .build();


        Customer customer = new Customer();
        customer.setId(new CustomerId(CUSTOMER_ID));

        List<Product> productList = new ArrayList<>();
        productList.add(new Product
                (new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))));
        productList.add(new Product
                (new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00"))));


        Restaurant restaurantResponse = Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(productList)
                .active(true)
                .build();




        Order order =  orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        order.setId(new OrderId(ORDER_ID));

        //Mocking repository
        when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
                .thenReturn(Optional.of(restaurantResponse));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
    }

    @Test
    public void testCreateOrder(){
        CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
        assertEquals(createOrderResponse.getOrderStatus(), OrderStatus.PENDING);
        assertEquals(createOrderResponse.getMessage(),"Order created successfully");
        assertNotNull(createOrderResponse.getOrderTrackingId());
    }

    @Test
    public void testCreteOrderWithWrongPrice(){
        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommandWrongPrice));

        assertEquals(orderDomainException.getMessage(), "Total price: 250.00"
                + " is not equal to Order items total: "+"200.00!");
    }

    @Test
    public void testCreteOrderWithWrongProductPrice(){
        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommandWrongProductPrice));

        assertEquals(orderDomainException.getMessage(), "Order item price: 60.00" +
                " is not valid for product " + PRODUCT_ID);
    }

    @Test
    public void testCreateOrderWithPassiveRestaurant(){

        List<Product> productList = new ArrayList<>();
        productList.add(new Product
                (new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))));
        productList.add(new Product
                (new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00"))));

        Restaurant restaurantResponse = Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(productList)
                .active(false)
                .build();


        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
                .thenReturn(Optional.of(restaurantResponse));

        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommand));

        assertEquals(orderDomainException.getMessage(), "Restaurant with id: " + RESTAURANT_ID +
        " is currently not active!");
    }

}
