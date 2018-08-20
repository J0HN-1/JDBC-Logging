package com.example.demoApp.unit;

import com.example.demoApp.model.User;
import com.example.demoApp.repository.UserRepository;
import com.example.demoApp.service.EntityNotFoundException;
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
    private static final User DUMMY_USER_1 = new User(1, "John", "E.", "some street", "Jerusalem");
    private static final User DUMMY_USER_2 = new User(2, "Nikita", "B.", "some other street", "Jerusalem");
    private static final UserDTO DUMMY_USER_1_DTO = new UserDTO(DUMMY_USER_1);
    private static final UserDTO DUMMY_USER_2_DTO = new UserDTO(DUMMY_USER_2);
    private static final UserDTO DUMMY_NEW_USER_1_DTO = new UserDTO(null, "John", "E.", "some street", "Jerusalem");

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService = new UserServiceImpl();

    @Test
    public void getUser() {
        when(userRepository.findById(DUMMY_USER_1_DTO.id)).thenAnswer(invocation -> Optional.of(DUMMY_USER_1));
        assertThat(userService.getUser(DUMMY_USER_1_DTO.id), equalTo(DUMMY_USER_1_DTO));
    }

    @Test
    public void getAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(DUMMY_USER_1, DUMMY_USER_2));
        assertThat(userService.getAllUsers(), equalTo(Arrays.asList(DUMMY_USER_1_DTO, DUMMY_USER_2_DTO)));
    }

    @Test(expected = EntityNotFoundException.class)
    public void getNonExistingUser() {
        when(userRepository.findById(any())).thenAnswer(invocation -> Optional.empty());
        userService.getUser(DUMMY_USER_1.getId());
    }

    @Test
    public void createUserEntity() {
        mockUserRespositorySaveMethod();
        assertThat(userService.saveUser(DUMMY_NEW_USER_1_DTO), is(DUMMY_USER_1_DTO));
    }

    @Test
    public void updateUserEntity() {
        mockUserRespositorySaveMethod();
        assertThat(userService.saveUser(DUMMY_USER_2_DTO), is(DUMMY_USER_2_DTO));
    }

    @Test
    public void deleteUserEntity() {
        when(userRepository.findById(DUMMY_USER_1.getId())).thenReturn(Optional.of(DUMMY_USER_1));
        userService.deleteUser(DUMMY_USER_1_DTO.id);
        verify(userRepository, times(1)).delete(DUMMY_USER_1);
        verify(userRepository, times(1)).findById(DUMMY_USER_1.getId());
    }

    private void mockUserRespositorySaveMethod() {
        when(userRepository.save(any())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            if(user.getId() == null) {
                user.setId(DUMMY_USER_1.getId());
            }
            return user;
        });
    }
}
