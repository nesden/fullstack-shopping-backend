package com.example.security.repository;

import com.example.security.model.CustomUser;
import com.example.security.model.Item;
import com.example.security.model.Order;
import com.example.security.model.OrderItem;
import com.example.security.repository.mapper.ItemMapper;
import com.example.security.repository.mapper.OrderItemsMapper;
import com.example.security.repository.mapper.OrderMapper;
import com.example.security.repository.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private static final String USERS_TABLE = "users";
    private static final String ITEMS_TABLE = "items";
    private static final String FAVORITE_TABLE = "favorite_to_users";
    private static final String ORDER_TABLE = "orders";
    private static final String ORDER_ITEMS_TABLE = "order_items";

    public Item findById(int itemId) {
        String sql = String.format("SELECT * FROM %s WHERE id = ?", ITEMS_TABLE);
        return jdbcTemplate.queryForObject(sql, new ItemMapper(), itemId);
    }

    public List<Item> findAllItems() {
        String sql = String.format("SELECT * FROM %s", ITEMS_TABLE);
        return jdbcTemplate.query(sql, new ItemMapper());
    }

    public List<Item> searchByName(String searchBar) {

        System.out.println(searchBar);
        String sql = String.format("SELECT * FROM %s WHERE name LIKE ?", ITEMS_TABLE);
        return jdbcTemplate.query(sql, new ItemMapper(), "%" + searchBar + "%");

    }

    public String addToFavorites(String username, int itemId) {
        //will happen when add to favorites is pressed
        String sql = String.format("INSERT INTO %s (user_username,item_id)" +
                "VALUES(?, ?)", FAVORITE_TABLE);
        jdbcTemplate.update(sql, username, itemId);
        return "was added to favorites successfully";
    }

    public String removeFromFavorites(String username, int itemId) {
        //will happen when remove from favorites is pressed
        String sql = String.format("DELETE FROM %s WHERE user_username=? AND item_id=?", FAVORITE_TABLE);
        jdbcTemplate.update(sql, username, itemId);
        return "was removed from favorites successfully";
    }


    public List<Item> findAllFavorites(String username) {

        String sql = String.format("""
                SELECT i.*
                FROM %s f
                JOIN %s i ON f.item_id = i.id
                WHERE f.user_username = ?
                """, FAVORITE_TABLE, ITEMS_TABLE);
        return jdbcTemplate.query(sql, new ItemMapper(), username);
    }

    public List<OrderItem> addItemsToOrder(int itemId, String username) {
        String userFinder = String.format("SELECT * FROM %s WHERE username=? ", USERS_TABLE);
        CustomUser user = jdbcTemplate.queryForObject(userFinder, new UserMapper(), username);

        String itemFinder = String.format("SELECT * FROM %s WHERE id=? ", ITEMS_TABLE);

        Item item = jdbcTemplate.queryForObject(itemFinder, new ItemMapper(), itemId);

        System.out.println(item);
        if (item.getAmount() <= 0) {
            throw new RuntimeException("error Item is out of stock");
        }
        lowerAmountHelper(itemId);
        String sqlOpenTemp = String.format("SELECT COUNT(*) FROM %s WHERE user_username = ? AND order_status = 'TEMP' ", ORDER_TABLE);//sqlOpenTemp
        Integer count = jdbcTemplate.queryForObject(sqlOpenTemp, Integer.class, username);// checks if theres an open order already

        Double itemPrice = item.getPrice();
        if (count < 1) {// there isnt an open order
            Integer newOrderId = lastOrderByUserHelper(username);
            String sql = String.format("INSERT INTO %s (order_id,user_username,order_date,order_shipping_address,order_status,full_price)" +
                    "VALUES(?,?,?,?,'TEMP',?)", ORDER_TABLE);// new order
            jdbcTemplate.update(sql, newOrderId, username, LocalDate.now(), user.getAddress(), itemPrice);

            String findNewOrderNumber = String.format("SELECT MAX(id) FROM %s", ORDER_TABLE);// id of the order we just made
            Integer newOrderNumber = jdbcTemplate.queryForObject(findNewOrderNumber, Integer.class);

            String orderItems = String.format("INSERT INTO %s (order_id,item_id,amount,price)" +
                    "VALUES(?,?,?,?)", ORDER_ITEMS_TABLE);// the first item in the new order
            jdbcTemplate.update(orderItems, newOrderNumber, itemId, 1, itemPrice);//new OrderItemsMapper()
            String ret = String.format("SELECT * FROM %s WHERE order_id = ?", ORDER_ITEMS_TABLE);
            return jdbcTemplate.query(ret, new OrderItemsMapper(), newOrderNumber);

        }
        String checkInTempOrder = String.format("""
                SELECT EXISTS (
                    SELECT 1
                    FROM %s oi
                    JOIN %s o ON o.id=oi.order_id
                    WHERE o.user_username = ? AND oi.item_id = ? AND o.order_status = 'TEMP'
                )
                """, ORDER_ITEMS_TABLE, ORDER_TABLE);
        Boolean isInTempOrder = jdbcTemplate.queryForObject(checkInTempOrder, Boolean.class, username, itemId);
        if (Boolean.TRUE.equals(isInTempOrder)) {// item is in the TEMP order
            String orderIdFinder = String.format("""
                    SELECT id
                    FROM %s
                    WHERE user_username = ? AND order_status = 'TEMP'
                    """, ORDER_TABLE);
            int orderId = jdbcTemplate.queryForObject(orderIdFinder, Integer.class, username);
            String orderItems = String.format("""
                        UPDATE %s oi
                        SET amount = amount + 1
                        WHERE order_id IN (
                            SELECT id FROM %s o
                            WHERE order_status = 'TEMP' AND user_username = ?
                        )
                        AND item_id = ?
                    """, ORDER_ITEMS_TABLE, ORDER_TABLE);

            jdbcTemplate.update(orderItems, username, itemId);
            Double fullCartPrice = calculatePrice(username, orderId);

            String fullPriceUpdate = String.format("UPDATE %s SET full_price = ? WHERE id = ?", ORDER_TABLE);
            jdbcTemplate.update(fullPriceUpdate, fullCartPrice, orderId);
            String ret = String.format("SELECT * FROM %s WHERE order_id = ?", ORDER_ITEMS_TABLE);
            return jdbcTemplate.query(ret, new OrderItemsMapper(), orderId);


        } else {// item isnt in TEMP order

            String orderIdFinder = String.format("SELECT id FROM %s WHERE user_username=? AND order_status ='TEMP'", ORDER_TABLE);    // finds the id of the temp order
            int orderId = jdbcTemplate.queryForObject(orderIdFinder, Integer.class, username);

            String orderItems = String.format("INSERT INTO %s (order_id,item_id,amount,price)" +
                    "VALUES(?,?,1,? )", ORDER_ITEMS_TABLE);
            jdbcTemplate.update(orderItems, orderId, itemId, itemPrice);
            String retSql = String.format("SELECT * FROM %s WHERE order_id = ?", ORDER_ITEMS_TABLE);
            List<OrderItem> ret = jdbcTemplate.query(retSql, new OrderItemsMapper(), orderId);
            Double fullCartPrice = calculatePrice(username, orderId);
            String fullPriceUpdate = String.format("UPDATE %s SET full_price = ? WHERE id = ? ", ORDER_TABLE);
            jdbcTemplate.update(fullPriceUpdate, fullCartPrice, orderId);
            return ret;
        }
    }

    public Double calculatePrice(String username, Integer orderId) {

        String sql = String.format("""
                    SELECT SUM(oi.price * oi.amount)
                    FROM %s oi
                    JOIN %s o ON oi.order_id = o.id
                    WHERE o.user_username = ? AND o.id = ?
                """, ORDER_ITEMS_TABLE, ORDER_TABLE);

        Double totalPrice = jdbcTemplate.queryForObject(sql, Double.class, username, orderId);
        totalPrice = totalPrice != null ? totalPrice : 0.0;
        return totalPrice;
    }

    public List<OrderItem> removeFromCart(int itemId, String username) {


        String sqlLower = String.format("""
                    UPDATE %s
                    SET amount = amount - 1
                    WHERE item_id = ?
                      AND amount > 0
                      AND order_id IN (
                          SELECT id FROM %s
                          WHERE user_username = ?
                            AND order_status = 'TEMP'
                      )
                """, ORDER_ITEMS_TABLE, ORDER_TABLE);

        // lower the item count in the cart of the user
        jdbcTemplate.update(sqlLower, itemId, username);
        raiseAmountHelper(itemId);//removing from cart will raise the amount in the shop
        String sqlIdFinder = String.format("SELECT id FROM %s WHERE user_username=? AND order_status = 'TEMP' ", ORDER_TABLE);
        int id = jdbcTemplate.queryForObject(sqlIdFinder, Integer.class, username);
        Double price = calculatePrice(username, id);
        String sqlDelete = String.format("""
                    DELETE FROM %s
                    WHERE item_id = ?
                      AND order_id = ?
                      AND amount <= 0
                """, ORDER_ITEMS_TABLE);
        jdbcTemplate.update(sqlDelete, itemId, id);
        String sqlPrice = String.format("UPDATE %s SET full_price=?  WHERE id=? ", ORDER_TABLE);
        jdbcTemplate.update(sqlPrice, price, id);

        String sql = String.format("SELECT * FROM %s WHERE order_id=? ", ORDER_ITEMS_TABLE);
        List<OrderItem> ret = jdbcTemplate.query(sql, new OrderItemsMapper(), id);


        String sqlCount = String.format("SELECT COUNT(*) FROM %s WHERE order_id = ?", ORDER_ITEMS_TABLE);
        Integer count = jdbcTemplate.queryForObject(sqlCount, Integer.class, id);//checking to see if the order is empty after the item was removed
        if (count< 1) {
            String sqlDeleteOrder = String.format("DELETE FROM %s WHERE id = ?", ORDER_TABLE);
            jdbcTemplate.update(sqlDeleteOrder, id);
        }
        return ret;
    }


    public String purchase(String username) {
        String userFinder = String.format("SELECT * FROM %s WHERE username=? ", USERS_TABLE);
        CustomUser user = jdbcTemplate.queryForObject(userFinder, new UserMapper(), username);
        String sqlAddress = String.format("UPDATE %s SET order_shipping_address=?  WHERE order_status='TEMP' AND user_username = ?  ", ORDER_TABLE);
        jdbcTemplate.update(sqlAddress, user.getAddress(), username);
        // making sure the address of the order goes to the user
        String tempCheck = String.format("SELECT COUNT(*) FROM %s WHERE user_username = ? AND order_status = 'TEMP' ", ORDER_TABLE);
        Integer count = jdbcTemplate.queryForObject(tempCheck, Integer.class, username);// checks if theres an open order already

        Boolean temp = count > 0;
        String purchaseSql = String.format("UPDATE %s SET order_status='CLOSE' WHERE user_username = ?", ORDER_TABLE);
        jdbcTemplate.update(purchaseSql, username);
        if (temp) {
            return "order was purchased successfully";
        }
        return "there weren't any open orders for this user";
    }

    public List<Order> getAllOrdersByUser(String username) {
        String sql = String.format("SELECT * FROM %s WHERE user_username = ?", ORDER_TABLE);
        return jdbcTemplate.query(sql, new OrderMapper(), username);
    }

    public List<Order> getAllOrders() {

        String sql = String.format("SELECT * FROM  %s", ORDER_TABLE);
        return jdbcTemplate.query(sql, new OrderMapper());
    }


    public Integer lastOrderByUserHelper(String username) {// finds the last orderid the user made
        try {
            String findOrderNumber = String.format("SELECT MAX(order_id) FROM %s WHERE user_username= ?", ORDER_TABLE); // the last order this user made
            Integer lastOrderId = jdbcTemplate.queryForObject(findOrderNumber, Integer.class, username);
            return lastOrderId + 1;
        } catch (Exception e) {
            return 1;
        }
    }


public List<Map<String, Object>> getItemsForOrder(int orderId) {
        String sql = String.format("SELECT i.id, i.name, i.image, oi.amount, oi.price" +
                " FROM %s oi" +
                " JOIN %s i ON i.id = oi.item_id" +
                " WHERE oi.order_id = ?" , ORDER_ITEMS_TABLE,ITEMS_TABLE);
        return jdbcTemplate.queryForList(sql, orderId);
    }

    public void raiseAmountHelper(int itemId) {
        // added this in case someone deletes their account makes it easier to restore the items from their temp order to the stock
        raiseAmountHelper(itemId, 1);
    }

    public void raiseAmountHelper(int itemId, int amount) {
        String sql = String.format("UPDATE %s SET amount = amount + ? WHERE id = ?", ITEMS_TABLE);
        jdbcTemplate.update(sql, amount, itemId);
    }


    public void lowerAmountHelper(int itemId) {//in shop
        //will happen everytime the add to cart will happen
        String sql = String.format("UPDATE %s SET amount = amount - 1 WHERE id = ?", ITEMS_TABLE);
        jdbcTemplate.update(sql ,itemId);
    }


}
