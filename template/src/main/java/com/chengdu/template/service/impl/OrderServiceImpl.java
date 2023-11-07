package com.chengdu.template.service.impl;

import com.chengdu.template.dao.templatesql.convertor.OrderPoBoConvertor;
import com.chengdu.template.dao.templatesql.mapper.OrderMapper;
import com.chengdu.template.dao.templatesql.po.OrderPo;
import com.chengdu.template.service.OrderService;
import com.chengdu.template.service.entity.OrderBo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    public OrderMapper orderMapper;

    @Override
    public boolean createOrder(OrderBo orderbo) {
        OrderPo orderPo = new OrderPo();
        OrderPoBoConvertor.orderBoToPo(orderbo, orderPo);
        int affectedRows = orderMapper.insert(orderPo);
        return affectedRows == 1;
    }

    @Override
    public OrderBo searchOrderById(Long orderId) {
        OrderPo orderPo = orderMapper.selectByPrimaryKey(orderId);
        OrderBo orderBo = new OrderBo();
        OrderPoBoConvertor.orderPoToBo(orderPo, orderBo);
        return orderBo;
    }
}
