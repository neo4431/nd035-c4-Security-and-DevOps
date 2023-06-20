package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);

    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void cannotFindUserWhenSubmitOrderTest() {
        when(userRepository.findByUsername("usernameNotExist")).thenReturn(null);

        final ResponseEntity<UserOrder> response = orderController.submit("usernameNotExist");

        Assert.assertNotNull(response);
        Assert.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void submitSuccessTest() {
        User user = setUpUser();

        UserOrder order = UserOrder.createFromCart(user.getCart());
        when(userRepository.findByUsername("username")).thenReturn(user);

        final ResponseEntity<UserOrder> response = orderController.submit("username");

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        Assert.assertEquals(order.getItems(), response.getBody().getItems());
        Assert.assertEquals(order.getTotal(), response.getBody().getTotal());
        Assert.assertEquals(BigDecimal.valueOf(2.0), response.getBody().getTotal());
        Assert.assertEquals(order.getUser(), response.getBody().getUser());
    }

    @Test
    public void cannotFindUserWhenGetOrderTest() {
        when(userRepository.findByUsername("usernameNotExist")).thenReturn(null);

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("usernameNotExist");

        Assert.assertNotNull(response);
        Assert.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getOrderSuccessTest() {
        User user = setUpUser();

        List<UserOrder> orders = new ArrayList<>();
        orders.add(UserOrder.createFromCart(user.getCart()));
        orders.add(UserOrder.createFromCart(user.getCart()));

        when(userRepository.findByUsername("username")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("username");

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        Assert.assertEquals(2, response.getBody().size());
    }

    private User setUpUser() {
        Item item = new Item();
        item.setId(1L);
        item.setName("item1");
        item.setPrice(BigDecimal.valueOf(1.0));
        item.setDescription("");

        Cart cart = new Cart();
        cart.addItem(item);
        cart.addItem(item);

        User user = new User();
        user.setId(1);
        user.setUsername("username");
        user.setPassword("password");
        user.setCart(cart);
        return user;
    }
}
