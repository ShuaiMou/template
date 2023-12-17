package com.chengdu.template.dao.templatesql.convertor;

import com.chengdu.template.dao.templatesql.po.OrderPo;
import com.chengdu.template.service.entity.OrderBo;
import org.springframework.beans.BeanUtils;

public final class OrderPoBoConvertor {
    public static void orderPoToBo(OrderPo po, OrderBo bo) {
        BeanUtils.copyProperties(po, bo);
    }

    public static void orderBoToPo(OrderBo bo, OrderPo po) {
        BeanUtils.copyProperties(bo, po);
    }

}
