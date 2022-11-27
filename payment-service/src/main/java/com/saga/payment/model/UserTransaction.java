package com.saga.payment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_transaction")
public class UserTransaction {

    @Id
    private Integer orderId;
    private Integer userId;
    private Integer amount;
}
