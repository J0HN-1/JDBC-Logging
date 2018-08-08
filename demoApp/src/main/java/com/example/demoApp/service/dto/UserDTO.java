package com.example.demoApp.service.dto;

import com.example.demoApp.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public class UserDTO {

    @JsonProperty(access = READ_ONLY)
    public Integer id;

    @NotBlank
    @Size(max = 50)
    public String firstName;

    @NotBlank
    @Size(max = 50)
    public String lastName;

    @NotBlank
    @Size(max = 100)
    public String address;

    @NotBlank
    @Size(max = 30)
    public String city;

    public UserDTO() {
    }

    public UserDTO(Integer id, String firstName, String lastName, String address, String city) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
    }

    public UserDTO(User user) {
        id = user.getId();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        address = user.getAddress();
        city = user.getCity();
    }
}
