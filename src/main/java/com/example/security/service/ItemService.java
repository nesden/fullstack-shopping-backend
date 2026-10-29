package com.example.security.service;

import com.example.security.model.CustomUser;
import com.example.security.model.Item;
import com.example.security.model.Order;
import com.example.security.model.OrderItem;
import com.example.security.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    public Item findById(int itemId) {
        return itemRepository.findById(itemId);
    }

    public List<Item> findAllItems() {
        return itemRepository.findAllItems();
    }

    public List<Item> searchByName(String searchBar) {

        return itemRepository.searchByName(searchBar.trim());
    }

    public List<Item> findAllFavorites(String username) {
        return itemRepository.findAllFavorites(username);
    }

    public String addToFavorites(String username, int itemId) {
        return itemRepository.addToFavorites(username, itemId);
    }

    public String removeFromFavorites(String username, int itemId) {
        return itemRepository.removeFromFavorites(username, itemId);
    }

    public List<OrderItem> addItemsToOrder(int itemId, String username) {
        return itemRepository.addItemsToOrder(itemId, username);
    }

    public List<OrderItem> removeFromCart(int itemId, String username) {

        return itemRepository.removeFromCart(itemId, username);
    }

    public String purchase(String username) {

        return itemRepository.purchase(username);

    }

    public List<Order> getAllOrdersByUser(String username) {
        return itemRepository.getAllOrdersByUser(username);
    }

    public List<Order> getAllOrders() {
        return itemRepository.getAllOrders();
    }
    public List<Map<String, Object>> getItemsForOrder(int orderId){
        return itemRepository.getItemsForOrder(orderId);
    }

}
