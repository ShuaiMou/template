package com.chengdu.template.dao.templatesql.mapper;

import com.chengdu.template.dao.templatesql.po.OrderPo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(OrderPo record);

    OrderPo selectByPrimaryKey(Long orderId);

    List<OrderPo> selectAll();

    int updateByPrimaryKey(OrderPo record);
}