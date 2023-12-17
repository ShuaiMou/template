package com.chengdu.template.web.dto;

import com.chengdu.template.web.dto.DtoBase;

import java.io.Serializable;

public class OrderDto extends DtoBase {
    private Long orderId;

    private Integer userId;

    private String status;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }
}
