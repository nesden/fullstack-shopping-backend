package com.example.security.controller;

import com.example.security.model.CustomUser;
import com.example.security.model.Item;
import com.example.security.model.Order;
import com.example.security.model.OrderItem;
import com.example.security.service.ItemService;
import com.example.security.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/items")
@CrossOrigin(origins = "http://localhost:3000")
public class ItemController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ItemService itemService;
//or hasRole('ADMIN')

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<List<Item>> findAllItems(@RequestHeader(value = "Authorization") String token) {
        try {
            return new ResponseEntity<>(itemService.findAllItems(), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{itemId}")
    public ResponseEntity<Item> findById(@RequestHeader(value = "Authorization") String token, @PathVariable int itemId) {
        try {
            //preAuthorise will make sure were logged in
            return new ResponseEntity<>(itemService.findById(itemId), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/search/{searchBar}")
    public ResponseEntity<List<Item>> searchByName(@RequestHeader(value = "Authorization") String token, @PathVariable String searchBar) {
        try {
            return new ResponseEntity<>(itemService.searchByName(searchBar), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/favorites/{itemId}")
    public ResponseEntity<String> addToFavorites(@RequestHeader(value = "Authorization") String token, @PathVariable int itemId) {
        try {
            String jwtToken = token.substring(7);
            String username = jwtUtil.extractUsername(jwtToken);
            String result = itemService.addToFavorites(username, itemId);
            if (result.contains("successfully")) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/favorites/{itemId}")
    public ResponseEntity<String> removeFromFavorites(@RequestHeader(value = "Authorization") String token, @PathVariable int itemId) {
        try {
            String jwtToken = token.substring(7);
            String username = jwtUtil.extractUsername(jwtToken);
            String result = itemService.removeFromFavorites(username, itemId);
            if (result.contains("successfully")) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/favorites")
    public ResponseEntity<List<Item>> findAllFavorites(@RequestHeader(value = "Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            String username = jwtUtil.extractUsername(jwtToken);
            return new ResponseEntity<>(itemService.findAllFavorites(username), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/cart/add/{itemId}")
    public ResponseEntity<List<OrderItem>> addItemsToOrder(@PathVariable int itemId, @RequestHeader(value = "Authorization") String token) {//
        try {
            String jwtToken = token.substring(7);
            String username = jwtUtil.extractUsername(jwtToken);
            return new ResponseEntity<>(itemService.addItemsToOrder(itemId, username), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/cart/remove/{itemId}")
    public ResponseEntity<List<OrderItem>> removeFromCart(@PathVariable int itemId, @RequestHeader(value = "Authorization") String token) {

        try {
            String jwtToken = token.substring(7);
            String username = jwtUtil.extractUsername(jwtToken);
            return new ResponseEntity<>(itemService.removeFromCart(itemId, username), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/cart/purchase")
    public ResponseEntity<String> purchase(@RequestHeader(value = "Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            String username = jwtUtil.extractUsername(jwtToken);
            String result = itemService.purchase(username);
            if (result.contains("successfully")) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrdersByUser(@RequestHeader(value = "Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            String username = jwtUtil.extractUsername(jwtToken);
            return new ResponseEntity<>(itemService.getAllOrdersByUser(username), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/orders/{orderId}/items")
    public ResponseEntity<List<Map<String, Object>>> getItemsForOrder(@PathVariable int orderId, @RequestHeader(value = "Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            jwtUtil.extractUsername(jwtToken);
            return new ResponseEntity<>(itemService.getItemsForOrder(orderId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/orders/all")
    public ResponseEntity<List<Order>> getAllOrders(@RequestHeader(value = "Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            jwtUtil.extractUsername(jwtToken);

            return new ResponseEntity<>(itemService.getAllOrders(), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
