package com.example.demoApp.service;

import com.example.demoApp.model.User;
import com.example.demoApp.service.dto.UserDTO;

import java.util.List;

public interface UserService {

    UserDTO getUser(int userId);

    List<UserDTO> getAllUsers();

    UserDTO saveUser(UserDTO userDTO);

    void deleteUser(int userId);

    User getUserEntity(int userId);
}
