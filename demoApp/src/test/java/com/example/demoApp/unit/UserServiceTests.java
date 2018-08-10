package com.example.demoApp.unit;

import com.example.demoApp.model.User;
import com.example.demoApp.repository.UserRepository;
import com.example.demoApp.service.UserService;
import com.example.demoApp.service.dto.UserDTO;
import com.example.demoApp.service.impl.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService = new UserServiceImpl();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getUser() {
        when(userRepository.findById(1)).thenAnswer(invocation ->
                Optional.of(new User(1, "John", "E.", "some street", "Jerusalem")));

        UserDTO user = userService.getUser(1);
        assertThat(user, equalTo(new UserDTO(1, "John", "E.", "some street", "Jerusalem")));
    }

    @Test
    public void getAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(
                new User(1, "John", "E.", "some street", "Jerusalem"),
                new User(2, "Nikita", "B.", "some other street", "Jerusalem")
        ));

        List<UserDTO> users = userService.getAllUsers();
        assertThat(users, equalTo(Arrays.asList(
                new UserDTO(1, "John", "E.", "some street", "Jerusalem"),
                new UserDTO(2, "Nikita", "B.", "some other street", "Jerusalem"))));
    }
}
