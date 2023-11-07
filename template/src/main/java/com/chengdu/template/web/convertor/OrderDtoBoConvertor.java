package com.chengdu.template.web.convertor;

import com.chengdu.template.service.entity.OrderBo;
import com.chengdu.template.web.dto.OrderDto;
import org.springframework.beans.BeanUtils;

public final class OrderDtoBoConvertor {
    public static void orderDtoToBo(OrderDto dto, OrderBo bo) {
        BeanUtils.copyProperties(dto, bo);
    }

    public static void orderBoToDto(OrderBo bo, OrderDto dto) {
        BeanUtils.copyProperties(bo, dto);
    }

}
