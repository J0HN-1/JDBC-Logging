package com.example.demoApp.service.impl;

import com.example.demoApp.model.User;
import com.example.demoApp.repository.UserRepository;
import com.example.demoApp.service.NoSuchEntityException;
import com.example.demoApp.service.UserService;
import com.example.demoApp.service.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Override
    public UserDTO getUser(int userId) {
        return new UserDTO(getUserEntity(userId));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(UserDTO::new).collect(Collectors.toList());
    }

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        return new UserDTO(userRepository.save(userFromDTO(userDTO)));
    }

    @Override
    public void deleteUser(int userId) {
        userRepository.delete(getUserEntity(userId));
    }

    private User getUserEntity(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NoSuchEntityException(User.class, userId));
    }

    private User userFromDTO(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.id);
        user.setFirstName(userDTO.firstName);
        user.setLastName(userDTO.lastName);
        user.setAddress(userDTO.address);
        user.setCity(userDTO.city);
        return user;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
