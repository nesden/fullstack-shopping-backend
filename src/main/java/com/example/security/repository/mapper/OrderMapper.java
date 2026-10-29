package com.example.security.repository.mapper;

import com.example.security.model.Item;
import com.example.security.model.Order;
import com.example.security.model.OrderStatus;
import jdk.jshell.Snippet;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderMapper implements RowMapper<Order> {
    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setOrderId(rs.getInt("order_id"));
        order.setUserUsername(rs.getString("user_username"));
        order.setOrderDate(rs.getDate("order_date").toLocalDate());
        order.setAddress(rs.getString("order_shipping_address"));
        order.setStatus(OrderStatus.valueOf(rs.getString("order_status")));
        order.setFullPrice(rs.getDouble("full_price"));
        return order;
    }
}

