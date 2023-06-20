package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void getAllItemsTest() {
        Item item = new Item();
        item.setId(1L);
        item.setName("item1");
        item.setPrice(BigDecimal.valueOf(1.0));
        item.setDescription("");

        List<Item> items = new ArrayList<>();
        items.add(item);

        when(itemRepository.findAll()).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItems();

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertEquals("item1", response.getBody().get(0).getName());
    }

    @Test
    public void getItemByIdTest() {
        Item item = new Item();
        item.setId(1L);
        item.setName("item1");
        item.setPrice(BigDecimal.valueOf(1.0));
        item.setDescription("");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertEquals("item1", response.getBody().getName());
    }

    @Test
    public void noItemFoundWhenGetItemByNameTest() {
        when(itemRepository.findByName("item1")).thenReturn(new ArrayList<>());
        when(itemRepository.findByName("item2")).thenReturn(null);

        ResponseEntity<List<Item>> response1 = itemController.getItemsByName("item1");
        Assert.assertNotNull(response1);
        Assert.assertEquals(404, response1.getStatusCodeValue());

        ResponseEntity<List<Item>> response2 = itemController.getItemsByName("item2");
        Assert.assertNotNull(response2);
        Assert.assertEquals(404, response2.getStatusCodeValue());
    }

    @Test
    public void getItemByNameSuccessTest() {
        Item item = new Item();
        item.setId(1L);
        item.setName("item1");
        item.setPrice(BigDecimal.valueOf(1.0));
        item.setDescription("");

        List<Item> items = new ArrayList<>();
        items.add(item);
        when(itemRepository.findByName("item1")).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItemsByName("item1");

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertEquals("item1", response.getBody().get(0).getName());
    }
}
