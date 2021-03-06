package com.qa.ims.controller;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.qa.ims.persistence.domain.Order;
import com.qa.ims.services.OrderServices;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTest {
	
	@Mock
	private OrderServices orderServices;
	
	@Spy
	@InjectMocks
	private OrderController orderController;
	
	@Test
	public void readAllTest() {
		OrderController orderController = new OrderController(orderServices);
		List<Order> orders = new ArrayList<>();
		orders.add(new Order(100L, 1L, Calendar.getInstance().getTime(), 1.50f, 1L, 5));
		orders.add(new Order(101L, 2L, Calendar.getInstance().getTime(), 2.00f, 2L, 10));
		orders.add(new Order(102L, 3L, Calendar.getInstance().getTime(), 2.50f, 3L, 15));
		Mockito.when(orderServices.readAll()).thenReturn(orders);
		assertEquals(orders, orderController.readAll());
	}
	
	
	@Test
	public void createTest() {
		System.out.println("---------CREATE TEST----------");
		String customer_id = "1";
		String item_id = "1";
		String quantity = "3";
		Mockito.doReturn(customer_id,item_id,quantity).when(orderController).getInput();
		Order order = new Order(1L, 1L, 3);
		Order savedOrder = new Order(100L, 1L, Calendar.getInstance().getTime(), 1.00f, 1L, 3);
		Mockito.doReturn(savedOrder).when(orderServices).create(order);
		System.out.println(savedOrder);
		assertEquals(savedOrder, orderController.create()); 
	}
	
	@Test
	public void updateAddTest() {
		System.out.println("---------UPDATE ADD TEST------------");
		String order_id = "100";
		String customer_id = "1";
		String item_id = "1";
		String quantity = "3";
		
		Mockito.doReturn(customer_id,order_id,item_id,quantity).when(orderController).getInput();;
		Order order = new Order(100L, 1L);
		Order savedOrder = new Order(100L, 1L, Calendar.getInstance().getTime(), 1.00f);
		Mockito.doReturn(savedOrder).when(orderServices).updateAddItem(order, 1L, 3);
		assertEquals(savedOrder, orderController.update());
	}
	
	@Test
	public void updateDelTest() {
		System.out.println("---------UPDATE DEL TEST------------");
		String order_id = "100";
		String customer_id = "1";
		String item_id = "1";
		
		Mockito.doReturn(customer_id,order_id,item_id).when(orderController).getInput();;
		Order order = new Order(100L, 1L, Calendar.getInstance().getTime(), 1.50f);
		Mockito.when(orderServices.updateDelItem(order, 1L)).thenReturn(null);
		assertEquals(null, orderController.update());
	}
	
	@Test
	public void deleteTest() {
		String order_id = "1";
		Mockito.doReturn(order_id).when(orderController).getInput();
		orderController.delete();
		Mockito.verify(orderServices, Mockito.times(1)).delete(1L);
	}

}
