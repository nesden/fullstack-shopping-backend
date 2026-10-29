package com.example.security.service;

import com.example.security.model.CustomUser;
import com.example.security.model.Role;
import com.example.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String register(CustomUser user) {
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty() || user.getLastName() == null || user.getLastName().trim().isEmpty() || user.getEmail() == null
                || user.getEmail().trim().isEmpty() || user.getUsername() == null || user.getUsername().trim().isEmpty() || user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return "User not created, first name, last name, email, username and password are required";
        }
        CustomUser userWithTheSameEmail = getUserByEmail(user.getEmail());
        CustomUser userWithTheSameUsername = getUserByUsername(user.getUsername());
        if (
                userWithTheSameEmail != null || userWithTheSameUsername != null) {
            return "User not created, This email or username already exists in the system.";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println("Encoded password: " + user.getPassword()); // Log the encoded password

        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }
        return userRepository.register(user);
    }

    public CustomUser getUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public CustomUser getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public List<CustomUser> getAllUsers() {
        return userRepository.findAllUsers();
    }

    public CustomUser updateUser(CustomUser updatedUser) {
        return userRepository.updateUser(updatedUser);
    }

    public String deleteUser(String username) {
        CustomUser registeredUser = userRepository.findUserByUsername(username);
        if (registeredUser == null) {
            return "The user with this username does not exist, so it cannot be deleted";
        }
        return userRepository.deleteUser(registeredUser.getUsername());
    }

}

