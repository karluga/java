package com.may.informatic.services;

import com.may.informatic.entities.User;
import com.may.informatic.repositories.UserRepository;
import com.may.informatic.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> fetchUsers() {
        return userRepository.findAll();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean registerUser(String username, String password, int role) {
        if (userRepository.findByUsername(username) != null) {
            return false;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.hashPassword(password));
        user.setRole(role);
        userRepository.save(user);
        return true;
    }
}