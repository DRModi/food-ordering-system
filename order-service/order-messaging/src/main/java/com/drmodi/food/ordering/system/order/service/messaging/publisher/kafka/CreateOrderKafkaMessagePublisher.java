package com.drmodi.food.ordering.system.order.service.messaging.publisher.kafka;

import com.drmodi.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.drmodi.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.drmodi.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.drmodi.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.drmodi.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import com.drmodi.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateOrderKafkaMessagePublisher implements OrderCreatedPaymentRequestMessagePublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
    private final OrderKafkaMessageHelper orderKafkaMessageHelper;

    public CreateOrderKafkaMessagePublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                            OrderServiceConfigData orderServiceConfigData,
                                            KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer,
                                            OrderKafkaMessageHelper orderKafkaMessageHelper) {
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaProducer = kafkaProducer;
        this.orderKafkaMessageHelper = orderKafkaMessageHelper;
    }

    @Override
    public void publish(OrderCreatedEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().getValue().toString();
        log.info("Received OrderCreatedEvent for oder id: {}", orderId);

        try {
            PaymentRequestAvroModel paymentRequestAvroModel = orderMessagingDataMapper
                    .orderCreatedEventToPaymentRequestAvroModel(domainEvent);


            kafkaProducer.send(orderServiceConfigData.getPaymentRequestTopicName(),
                    orderId,
                    paymentRequestAvroModel,
                    orderKafkaMessageHelper.getKafkaCallback(orderServiceConfigData.getPaymentResponseTopicName(),
                            paymentRequestAvroModel,
                            orderId,
                            "PaymentRequestAvroModel")
                    );
            log.info("PaymentRequestAvroModel sent to kafka for order id: {}", paymentRequestAvroModel.getOrderId());

        } catch (Exception e) {
            log.error("Error while sending PaymentRequestAvroModel message"+
                    " to kafka with order id: {}, error: {}", orderId, e.getMessage());
        }

    }

//    private ListenableFutureCallback<SendResult<String, PaymentRequestAvroModel>> getKafkaCallback(
//            String paymentRequestTopicName, PaymentRequestAvroModel paymentRequestAvroModel) {
//
//        return new ListenableFutureCallback<SendResult<String, PaymentRequestAvroModel>>() {
//            @Override
//            public void onFailure(Throwable ex) {
//                log.error("Error while sending PaymentRequestAvroModel "+
//                        "message {} to topic {}", paymentRequestAvroModel.toString(), paymentRequestTopicName, ex);
//            }
//
//            @Override
//            public void onSuccess(SendResult<String, PaymentRequestAvroModel> result) {
//                RecordMetadata metadata = result.getRecordMetadata();
//                log.info("Received successful response from kafka for order id: {}" +
//                        " Topic: {} Partition: {} Offset: {} Timestamp: {}",
//                        paymentRequestAvroModel.getOrderId(),
//                        metadata.topic(),
//                        metadata.partition(),
//                        metadata.offset(),
//                        metadata.timestamp());
//            }
//        };
//    }
}
