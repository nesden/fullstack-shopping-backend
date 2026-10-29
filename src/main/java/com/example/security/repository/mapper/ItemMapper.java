package com.example.security.repository.mapper;

import com.example.security.model.Item;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemMapper implements RowMapper<Item> {
    @Override
    public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
        Item item = new Item();
        item.setId(rs.getInt("id"));
        item.setName(rs.getString("name"));
        item.setImage(rs.getString("image"));
        item.setPrice(rs.getDouble("price"));
        item.setAmount(rs.getInt("amount"));
        return item;
    }
}
