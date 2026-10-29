package com.example.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public class Order {
    private Integer id;
    @JsonProperty(value = "order_id")
    private Integer orderId;
    @JsonProperty(value = "user_username")
    private String userUsername;
    @JsonProperty(value = "order_date")
    private LocalDate orderDate;
    @JsonProperty(value = "order_shipping_address")
    private String address;
    private OrderStatus status;
    @JsonProperty(value = "full_price")
    private Double fullPrice;


    public Order(Integer id, Integer orderId, String userUsername, LocalDate orderDate, String address, OrderStatus status, Double fullPrice) {
        this.id = id;
        this.orderId = orderId;
        this.userUsername = userUsername;
        this.orderDate = orderDate;
        this.address = address;
        this.status = status;
        this.fullPrice = fullPrice;

    }

    public Order() {
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

    public String getUserUsername() {
        return userUsername;
    }

    public void setUserUsername(String userUsername) {
        this.userUsername = userUsername;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Double getFullPrice() {
        return fullPrice;
    }

    public void setFullPrice(Double fullPrice) {
        this.fullPrice = fullPrice;
    }

//    public List<OrderItem> getItems() {
//        return items;
//    }
//
//    public void setItems(List<OrderItem> items) {
//        this.items = items;
//    }

}
