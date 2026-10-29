package com.example.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderItem {
    private Integer id;
    @JsonProperty(value = "order_id")
    private Integer orderId;
    @JsonProperty(value = "item_id")
    private Integer itemId;
    private Integer amount;
    private Double price;

    public OrderItem(Integer id, Integer orderId, Integer itemId, Integer amount, Double price) {
        this.id = id;
        this.orderId = orderId;
        this.itemId = itemId;
        this.amount = amount;
        this.price = price;
    }

    public OrderItem() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
