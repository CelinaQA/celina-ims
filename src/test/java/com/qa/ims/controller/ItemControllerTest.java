package com.qa.ims.controller;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.qa.ims.persistence.domain.Item;
import com.qa.ims.services.ItemServices;

@RunWith(MockitoJUnitRunner.class)
public class ItemControllerTest {
	
	@Mock
	private ItemServices itemServices;
	
	@Spy
	@InjectMocks
	private ItemController itemController;
	
	@Test
	public void readAllTest() {
		ItemController itemController = new ItemController(itemServices);
		List<Item> items = new ArrayList<>();
		items.add(new Item("bread", 100, 1.99f));
		items.add(new Item("cookies", 50, 5f));
		items.add(new Item("chocolate", 200, 10f));
		Mockito.when(itemServices.readAll()).thenReturn(items);
		assertEquals(items, itemController.readAll());
	}
	
	@Test
	public void createTest() {
		String name = "sugar";
		String stock = "20";
		String price = "3.99";
		Mockito.doReturn(name, stock, price).when(itemController).getInput();
		Item item = new Item(name, 20, 3.99f);
		Item savedItem = new Item(1L, "sugar", 20, 3.99f);
		System.out.println(itemServices.create(item));
		Mockito.when(itemServices.create(item)).thenReturn(savedItem);
		assertEquals(savedItem, itemController.create());
	}
	
	@Test
	public void updateTest() {
		String id = "1";
		String name = "sugar";
		String stock = "20";
		String price = "3.99";
		Mockito.doReturn(id, name, stock, price).when(itemController).getInput();
		Item item = new Item(1L, name, 20, 3.99f);
		Mockito.when(itemServices.update(item)).thenReturn(item);
		assertEquals(item, itemController.update());
	}
	
	@Test
	public void deleteTest() {
		String id = "1";
		Mockito.doReturn(id).when(itemController).getInput();
		itemController.delete();
		Mockito.verify(itemServices, Mockito.times(1)).delete(1L);
	}

}
