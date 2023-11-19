package com.chengdu.template.service;

import com.chengdu.template.service.entity.OrderBo;
import org.springframework.transaction.annotation.Transactional;


public interface OrderService {
    boolean createOrder(OrderBo orderbo);
    OrderBo searchOrderById(Long orderId);
}
