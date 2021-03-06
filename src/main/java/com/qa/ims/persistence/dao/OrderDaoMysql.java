package com.qa.ims.persistence.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qa.ims.persistence.domain.Order;
import com.qa.ims.utils.DBUtils;

public class OrderDaoMysql implements OrderDao<Order>{
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	public Order modelFromResultSet(ResultSet resultSet) throws SQLException {
		Long id = resultSet.getLong("order_id");
		Long customer_id = resultSet.getLong("customer_id");
		Date date_placed = resultSet.getDate("date_placed");
		Float totalPrice = resultSet.getFloat("SUM(ol.total_price)");

		return new Order(id, customer_id, date_placed, totalPrice);
	}
	
	//READ FUNCTION--------------------------------------------
	
	@Override
	public List<Order> readAll() {
		try (Connection connection = DBUtils.getInstance().getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT o.date_placed,o.order_id,o.customer_id,SUM(ol.total_price) from orderlines ol JOIN orders o ON o.order_id=ol.order_id JOIN items i ON i.item_id=ol.item_id GROUP BY o.order_id ORDER BY o.order_id;");) {
			List<Order> orders = new ArrayList<>();
			while (resultSet.next()) {
				orders.add(modelFromResultSet(resultSet));
			}
			return orders;
		} catch (SQLException e) {
			LOGGER.debug(e.getStackTrace());
			LOGGER.error(e.getMessage());
		}
		return new ArrayList<>();
	}
	
	public Order readLatest() {
		try (Connection connection = DBUtils.getInstance().getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT o.date_placed,o.order_id,o.customer_id,SUM(ol.total_price) from orderlines ol JOIN orders o ON o.order_id=ol.order_id JOIN items i ON i.item_id=ol.item_id WHERE o.order_id=(SELECT order_id FROM orders ORDER BY order_id DESC LIMIT 1) GROUP BY o.order_id;");) {
			resultSet.next();
			return modelFromResultSet(resultSet);
		} catch (Exception e) {
			LOGGER.debug(e);
			LOGGER.error(e.getMessage());
		}
		return null;
	}
	
	public Order readOrder(Long id) {
		try (Connection connection = DBUtils.getInstance().getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT o.date_placed,ol.order_id,o.customer_id,SUM(ol.total_price) from orderlines ol JOIN orders o ON o.order_id=ol.order_id JOIN items i ON i.item_id=ol.item_id WHERE o.order_id=" + id+";");) {
			resultSet.next();
			return modelFromResultSet(resultSet);
		} catch (Exception e) {
			LOGGER.debug(e.getStackTrace());
			LOGGER.error(e.getMessage());
		}
		return null;
	}
	
	
	//CREATE FUNCTION------------------------------------------
	
	//Date placed is left null to automatically take default value as current date
	@Override
	public Order create(Order order) {
		try (Connection connection = DBUtils.getInstance().getConnection();
				Statement statement = connection.createStatement();) {
			
			String getPrice = "SELECT price FROM items WHERE item_id="+order.getItem_id();
			ResultSet resultPrice = statement.executeQuery(getPrice);
			while(resultPrice.next()) {
				Float price = resultPrice.getFloat("price");
				order.setTotalPrice(price);
			}
			Float totalPrice = order.getTotalPrice()*order.getQuantity();
			order.setTotalPrice(totalPrice);
			
			String createOrder = "INSERT INTO orders(customer_id) VALUES(" + order.getCustomer_id() + 
					"); INSERT INTO orderlines(order_id,item_id,quantity,total_price) VALUES((SELECT order_id FROM orders ORDER BY order_id DESC LIMIT 1),"
					+order.getItem_id()+","+order.getQuantity()+","+order.getTotalPrice()+");";
			statement.executeUpdate(createOrder);
			
			return readLatest();
		} catch (Exception e) {
			LOGGER.debug(e.getStackTrace());
			LOGGER.error(e.getMessage());
		}
		return null; 
	}
	
	//UPDATE FUNCTIONS-----------------------------------------

	@Override
	public Order updateDelItem(Order order, Long item_id) {
		try (Connection connection = DBUtils.getInstance().getConnection();
				Statement statement = connection.createStatement();) {
			statement.executeUpdate("DELETE FROM orderlines WHERE order_id=" + order.getId() + " AND item_id="+ item_id + ";");
			return readOrder(order.getId());
		} catch (Exception e) {
			LOGGER.debug(e.getStackTrace());
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	@Override
	public Order updateAddItem(Order order, Long item_id, Integer item_quantity) {
		try (Connection connection = DBUtils.getInstance().getConnection();
				Statement statement = connection.createStatement();) {
			String getPrice = "SELECT price FROM items WHERE item_id="+item_id;
			ResultSet resultPrice = statement.executeQuery(getPrice);
			
			Float newPrice = null;
			while(resultPrice.next()) {
				newPrice = resultPrice.getFloat("price")*item_quantity;
			}
			
			statement.executeUpdate("INSERT INTO orderlines(order_id,item_id,quantity,total_price) VALUES("+order.getId()+","+item_id+","+item_quantity+","+newPrice+");");
			return readOrder(order.getId());
		} catch (Exception e) {
			LOGGER.debug(e.getStackTrace());
			LOGGER.error(e.getMessage());
		}
		return null;
	}
	
	//DELETE FUNCTION-----------------------------------------
	
	@Override
	public void delete(long id) {
		try (Connection connection = DBUtils.getInstance().getConnection();
				Statement statement = connection.createStatement();) {
			statement.executeUpdate("DELETE FROM orderlines WHERE order_id=" + id + "; DELETE FROM orders WHERE order_id=" + id + ";");
		} catch (Exception e) {
			LOGGER.debug(e.getStackTrace());
			LOGGER.error(e.getMessage());
		}
	}
}
