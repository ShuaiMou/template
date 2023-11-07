package com.chengdu.template.web.controller;

import com.chengdu.template.common.JsonResult;
import com.chengdu.template.service.OrderService;
import com.chengdu.template.service.entity.OrderBo;
import com.chengdu.template.web.convertor.OrderDtoBoConvertor;
import com.chengdu.template.web.dto.OrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @GetMapping(path = "/search-by-id/{id}")
    public JsonResult<OrderDto> searchOrderByOrderId(@PathVariable("id") Long orderId) {
        OrderBo orderBo = orderService.searchOrderById(orderId);

        OrderDto orderDto = new OrderDto();
        OrderDtoBoConvertor.orderBoToDto(orderBo, orderDto);
        return JsonResult.success(orderDto);
    }

    @PostMapping(path = "/create-order")
    public JsonResult<Boolean> createOrder(@RequestBody OrderDto orderDto) {
        OrderBo orderBo = new OrderBo();
        OrderDtoBoConvertor.orderDtoToBo(orderDto, orderBo);
        boolean res = orderService.createOrder(orderBo);
        return JsonResult.success(res);
    }
}
