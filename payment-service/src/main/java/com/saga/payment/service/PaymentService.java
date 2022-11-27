package com.saga.payment.service;

import com.common.dtos.dto.OrderRequestDto;
import com.common.dtos.dto.PaymentRequestDto;
import com.common.dtos.event.OrderEvent;
import com.common.dtos.event.PaymentEvent;
import com.common.dtos.event.PaymentStatus;
import com.saga.payment.model.UserBalance;
import com.saga.payment.model.UserTransaction;
import com.saga.payment.repository.UserBalanceRepository;
import com.saga.payment.repository.UserTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PaymentService {

    @Autowired
    private UserBalanceRepository userBalanceRepository;

    @Autowired
    private UserTransactionRepository userTransactionRepository;

    @PostConstruct
    public void initUserBalanceInBb(){
        userBalanceRepository.saveAll(Stream.of(
                new UserBalance(101,5000),
                new UserBalance(102,3000),
                new UserBalance(103,4200),
                new UserBalance(104,20000),
                new UserBalance(105,999)
        ).collect(Collectors.toList()));
    }


    @Transactional
    public PaymentEvent newOrderEvent(OrderEvent orderEvent) {
        OrderRequestDto orderRequestDto=orderEvent.getOrderRequestDto();
        PaymentRequestDto paymentRequestDto=new PaymentRequestDto(
                orderRequestDto.getOrderId(),
                orderRequestDto.getUserId(),
                orderRequestDto.getAmount());
        return userBalanceRepository.findById(orderRequestDto.getUserId())
                .filter(obj->obj.getPrice()>orderRequestDto.getAmount())
                .map(obj->{
                    obj.setPrice(obj.getPrice()-orderRequestDto.getAmount());
                    userTransactionRepository.save(new UserTransaction(
                            orderRequestDto.getOrderId(),
                            orderRequestDto.getUserId(),
                            orderRequestDto.getAmount()));
                    return new PaymentEvent(paymentRequestDto, PaymentStatus.PAYMENT_COMPLETED);
                })
                .orElse(new PaymentEvent(paymentRequestDto,PaymentStatus.PAYMENT_FAILED));

    }

    @Transactional
    public void cancelOrderEvent(OrderEvent orderEvent) {
        userTransactionRepository.findById(orderEvent.getOrderRequestDto().getOrderId())
                .ifPresent(obj->{
                    userTransactionRepository.delete(obj);
                    userTransactionRepository.findById(obj.getUserId())
                            .ifPresent(us->us.setAmount(us.getAmount()+obj.getAmount()));
                });
    }
}
