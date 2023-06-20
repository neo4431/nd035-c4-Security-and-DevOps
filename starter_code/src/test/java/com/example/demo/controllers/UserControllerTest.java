package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import org.junit.Assert;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.util.*;

import static org.mockito.Mockito.*;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void createUserSuccessTest() {
        when(encoder.encode("password")).thenReturn("ThisIsHashed");
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("username");
        userRequest.setPassword("password");
        userRequest.setConfirmPassword("password");

        final ResponseEntity<User> response = userController.createUser(userRequest);

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();

        Assert.assertEquals(0, user.getId());
        Assert.assertEquals("username", user.getUsername());
        Assert.assertEquals("ThisIsHashed", user.getPassword());
    }

    @Test
    public void createUserFailedTest() {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("username");
        userRequest.setPassword("abc");
        userRequest.setConfirmPassword("abc");

        final ResponseEntity<User> response = userController.createUser(userRequest);
        Assert.assertNotNull(response);
        Assert.assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void findUserByIdTest() {
        User u = new User();
        u.setId(1);
        u.setUsername("username");
        u.setPassword("password");
        u.setCart(new Cart());

        when(userRepository.findById(1L)).thenReturn(Optional.of(u));

        final ResponseEntity<User> response = userController.findById(1L);

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();

        Assert.assertEquals(1, user.getId());
        Assert.assertEquals("username", user.getUsername());
    }

    @Test
    public void findUserByUsername() {
        User u = new User();
        u.setId(1);
        u.setUsername("username");
        u.setPassword("password");
        u.setCart(new Cart());

        when(userRepository.findByUsername("username")).thenReturn(u);

        final ResponseEntity<User> response = userController.findByUserName("username");

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();

        Assert.assertEquals(1, user.getId());
        Assert.assertEquals("username", user.getUsername());
    }

}
