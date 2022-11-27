package com.saga.order.service;

import com.common.dtos.dto.OrderRequestDto;
import com.common.dtos.event.OrderStatus;
import com.saga.order.model.PurchaseOrder;
import com.saga.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusPublisher orderStatusPublisher;

    @Transactional
    public PurchaseOrder createOrder(OrderRequestDto orderRequestDto) {
        PurchaseOrder order=orderRepository.save(convertTo(orderRequestDto));
        orderRequestDto.setOrderId(order.getId());
        orderStatusPublisher.publishOrderEvent(orderRequestDto,OrderStatus.ORDER_CREATED);
        return order;
    }

    public List<PurchaseOrder> getAllOrders(){
        return orderRepository.findAll();
    }

    private PurchaseOrder convertTo(OrderRequestDto orderRequestDto) {
        PurchaseOrder purchaseOrder=new PurchaseOrder();
        purchaseOrder.setProductId(orderRequestDto.getProductId());
        purchaseOrder.setUserId(orderRequestDto.getUserId());
        purchaseOrder.setOrderStatus(OrderStatus.ORDER_CREATED);
        purchaseOrder.setPrice(orderRequestDto.getAmount());
        return purchaseOrder;
    }
}
