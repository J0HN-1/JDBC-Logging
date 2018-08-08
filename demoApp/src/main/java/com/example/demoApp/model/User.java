package com.example.demoApp.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    private int version;

    @NotBlank
    @Size(max = 50)
    @Column(name = "fname", nullable = false)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    @Column(name = "lname", nullable = false)
    private String lastName;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String address;

    @NotBlank
    @Size(max = 30)
    @Column(nullable = false)
    private String city;

    @OneToMany(mappedBy = "owner")
    private Set<Account> accounts = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

}
