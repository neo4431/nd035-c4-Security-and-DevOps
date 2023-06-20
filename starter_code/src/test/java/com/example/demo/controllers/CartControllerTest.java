package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void cannotFindUserWhenAddToCartTest() {
        when(userRepository.findByUsername("usernameNotExist")).thenReturn(null);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("usernameNotExist");
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(1);

        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        Assert.assertNotNull(response);
        Assert.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void cannotFindItemWhenAddToCartTest() {
        when(userRepository.findByUsername("username")).thenReturn(new User());
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("username");
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);

        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        Assert.assertNotNull(response);
        Assert.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void addToCartSuccessTest() {
        User user = new User();
        user.setId(1);
        user.setUsername("username");
        user.setPassword("password");
        user.setCart(new Cart());

        Item item = new Item();
        item.setId(1L);
        item.setName("item1");
        item.setPrice(BigDecimal.valueOf(1.0));
        item.setDescription("");

        when(userRepository.findByUsername("username")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("username");
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(2);

        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertEquals(2, response.getBody().getItems().size());
    }



    @Test
    public void cannotFindUserWhenRemoveFromcartTest() {
        when(userRepository.findByUsername("usernameNotExist")).thenReturn(null);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("usernameNotExist");
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(1);

        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

        Assert.assertNotNull(response);
        Assert.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void cannotFindItemWhenRemoveFromcartTest() {
        when(userRepository.findByUsername("username")).thenReturn(new User());
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("username");
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);

        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

        Assert.assertNotNull(response);
        Assert.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void removeFromcartSuccessTest() {
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

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("username");
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(2);

        when(userRepository.findByUsername("username")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertEquals(0, response.getBody().getItems().size());
    }
}
