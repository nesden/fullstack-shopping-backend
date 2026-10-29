//package com.example.security.controller;
//
//import com.example.security.model.CustomUser;
//import com.example.security.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/admin")
//@CrossOrigin(origins = "http://localhost:3000")
//public class AdminController {
//
//    @Autowired
//    private UserService userService;
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping(value = "/all-users")
//    public ResponseEntity<List<CustomUser>> getAllUsers() {
//        try{
//            List<CustomUser> users = userService.getAllUsers();
//            return ResponseEntity.ok(users);
//        } catch (Exception e){
//            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @DeleteMapping("/delete-user/{username}")
//    public ResponseEntity<String> deleteAnotherUser(@PathVariable String username) {
//        try {
//            String result = userService.deleteUser(username);
//            if (result.contains("successfully")) {
//                return new ResponseEntity(result, HttpStatus.OK);
//            }
//            return new ResponseEntity(result, HttpStatus.BAD_REQUEST);
//        } catch (Exception e) {
//            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//}
//
