package com.saga.order.service;

import com.common.dtos.dto.OrderRequestDto;
import com.common.dtos.event.OrderEvent;
import com.common.dtos.event.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

@Service
public class OrderStatusPublisher {

    @Autowired
    private Sinks.Many<OrderEvent> orderSinks;

    public void publishOrderEvent(OrderRequestDto orderDto, OrderStatus orderStatus){
        OrderEvent orderEvent=new OrderEvent(
                orderDto,
                orderStatus
        );
        orderSinks.tryEmitNext(orderEvent);

    }

}
