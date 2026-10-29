package com.example.security.repository;

import com.example.security.model.CustomUser;
import com.example.security.repository.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ItemRepository itemRepository;
    private static final String USERS_TABLE = "users";
    private static final String ORDER_TABLE = "orders";
    private static final String ORDER_ITEMS_TABLE = "order_items";


    public String register(CustomUser user) {
        String sql = String.format("INSERT INTO %s (first_name, last_name, email, phone, address, username, password, role) VALUES (?,?,?,?,?,?,?,?)", USERS_TABLE);
        jdbcTemplate.update(sql, user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhone(), user.getAddress(), user.getUsername(), user.getPassword(), user.getRole().name());
        return "User registered successfully";
    }

    public CustomUser findUserByUsername(String username) {
        try {
            String sql = String.format("SELECT * FROM %s WHERE username = ?", USERS_TABLE);
            CustomUser user = jdbcTemplate.queryForObject(sql, new UserMapper(), username);
            return user;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public CustomUser findUserByEmail(String email) {
        try {
            String sql = String.format("SELECT * FROM %s WHERE email = ?", USERS_TABLE);
            CustomUser user = jdbcTemplate.queryForObject(sql, new UserMapper(), email);
            return user;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<CustomUser> findAllUsers() {
        String sql = String.format("SELECT * FROM %s", USERS_TABLE);
        List<CustomUser> users = jdbcTemplate.query(sql, new UserMapper());
        return users;
    }

    public CustomUser updateUser(CustomUser user) {
        String sql = String.format("UPDATE %s SET first_name = ?, last_name = ?, email = ?, phone = ?, address = ? WHERE username = ?", USERS_TABLE);
        jdbcTemplate.update(sql, user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhone(), user.getAddress(), user.getUsername());
        return findUserByUsername(user.getUsername());
    }

    public String deleteUser(String username) {
        deleteOrdersHelper(username);
        String sql = String.format("DELETE FROM %s WHERE username = ?", USERS_TABLE);
        jdbcTemplate.update(sql, username);
        return "User deleted successfully";

    }

    public void deleteOrdersHelper(String username) {
        String tempOrderSql = String.format("""
                    SELECT id FROM %s
                    WHERE user_username = ? AND order_status = 'TEMP'
                """, ORDER_TABLE);

        Integer tempOrderId = null;
        try {
            tempOrderId = jdbcTemplate.queryForObject(tempOrderSql, Integer.class, username);
        } catch (Exception e) {
            System.out.println("there is no temp order for this user " + e.getMessage());

        }

        if (tempOrderId != null) {
            String itemsSql = String.format("""
                        SELECT item_id, amount FROM %s
                        WHERE order_id = ?
                    """, ORDER_ITEMS_TABLE);

            List<Map<String, Object>> itemRows = jdbcTemplate.queryForList(itemsSql, tempOrderId);

            for (Map<String, Object> row : itemRows) {
                int itemId = (int) row.get("item_id");
                int amount = (int) row.get("amount");

                // makes sure the temp items will be back in the shop before being deleted
                itemRepository.raiseAmountHelper(itemId, amount);
            }

        }

        String orderIdsSql = String.format("""
                    SELECT id FROM %s WHERE user_username = ?
                """, ORDER_TABLE);
        List<Integer> orderIds = jdbcTemplate.queryForList(orderIdsSql, Integer.class, username);
//deleting the items from the orders before we get to delete the orders
        for (int orderId : orderIds) {
            String deleteItemsSql = String.format("""
                        DELETE FROM %s WHERE order_id = ?
                    """, ORDER_ITEMS_TABLE);
            jdbcTemplate.update(deleteItemsSql, orderId);
        }


// Delete all orders for this user
        String deleteSql = String.format("DELETE FROM %s WHERE user_username = ?", ORDER_TABLE);
        jdbcTemplate.update(deleteSql, username);
    }
}
