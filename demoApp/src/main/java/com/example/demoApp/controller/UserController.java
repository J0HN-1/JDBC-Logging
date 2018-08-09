package com.example.demoApp.controller;

import com.example.demoApp.exception.RequestBindingException;
import com.example.demoApp.service.UserService;
import com.example.demoApp.service.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static com.example.demoApp.DemoAppApplication.REST_API_PREFIX;
import static org.springframework.http.MediaType.*;

@RestController
@RequestMapping(value = REST_API_PREFIX + "/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public UserDTO getUser(@PathVariable int id) {
        return userService.getUser(id);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new RequestBindingException(bindingResult);
        }
        UserDTO newUser = userService.saveUser(user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newUser.id)
                .toUri();
        return ResponseEntity.created(location).body(newUser);
    }

    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public UserDTO updateUser(@PathVariable int id, @Valid @RequestBody UserDTO user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new RequestBindingException(bindingResult);
        }
        user.id = id;
        return userService.saveUser(user);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
    }
}
