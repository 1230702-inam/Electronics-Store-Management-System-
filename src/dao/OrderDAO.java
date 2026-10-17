package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import models.Order;
import models.OrderItem;
import utils.DBConnection;

public class OrderDAO {

    // Get all orders
    public ArrayList<Order> getAllOrders() {

        ArrayList<Order> orders = new ArrayList<>();

        String sql = "SELECT * FROM orders ORDER BY Order_Date DESC";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Order order = new Order();
                order.setOrderId(rs.getInt("Order_ID"));
                order.setCustomerId(rs.getInt("Customer_ID"));
                order.setOrderDate(rs.getDate("Order_Date"));
                order.setTotalAmount(rs.getDouble("Total_Amount"));
                order.setStatus(rs.getString("Order_Status"));

                orders.add(order);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return orders;
    }

    // Get orders by customer ID
    public ArrayList<Order> getOrdersByCustomer(int customerId) {

        ArrayList<Order> orders = new ArrayList<>();

        String sql = "SELECT * FROM orders " +
                     "WHERE Customer_ID = ? " +
                     "ORDER BY Order_Date DESC";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Order order = new Order();
                order.setOrderId(rs.getInt("Order_ID"));
                order.setCustomerId(rs.getInt("Customer_ID"));
                order.setOrderDate(rs.getDate("Order_Date"));
                order.setTotalAmount(rs.getDouble("Total_Amount"));
                order.setStatus(rs.getString("Order_Status"));

                orders.add(order);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return orders;
    }

    // Get order by ID
    public Order getOrderById(int orderId) {

        String sql = "SELECT * FROM orders WHERE Order_ID = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("Order_ID"));
                order.setCustomerId(rs.getInt("Customer_ID"));
                order.setOrderDate(rs.getDate("Order_Date"));
                order.setTotalAmount(rs.getDouble("Total_Amount"));
                order.setStatus(rs.getString("Order_Status"));
                return order;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Create new order - بيرجع الـ Order_ID الجديد
    public int createOrder(int customerId, double totalAmount) {

        String sql = "INSERT INTO orders " +
                     "(Customer_ID, Employee_ID, Order_Date, Order_Status, Total_Amount) " +
                     "VALUES (?, 1, CURDATE(), 'Pending', ?)";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                sql, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {

            ps.setInt(1, customerId);
            ps.setDouble(2, totalAmount);

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    // Add order item
    public boolean addOrderItem(int orderId,
                                int productId,
                                int quantity,
                                double unitPrice) {

        String sql = "INSERT INTO order_item " +
                     "(Order_ID, Product_ID, Quantity, Unit_Price) " +
                     "VALUES (?, ?, ?, ?)";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, orderId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            ps.setDouble(4, unitPrice);

            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Get order items by order ID
    public ArrayList<OrderItem> getOrderItems(int orderId) {

        ArrayList<OrderItem> items = new ArrayList<>();

        String sql = "SELECT * FROM order_item WHERE Order_ID = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                OrderItem item = new OrderItem();
                item.setOrderItemId(rs.getInt("Order_Item_ID"));
                item.setOrderId(rs.getInt("Order_ID"));
                item.setProductId(rs.getInt("Product_ID"));
                item.setQuantity(rs.getInt("Quantity"));
                item.setUnitPrice(rs.getDouble("Unit_Price"));

                items.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    // Update order status
    public boolean updateOrderStatus(int orderId, String status) {

        String sql = "UPDATE orders SET Order_Status = ? " +
                     "WHERE Order_ID = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, status);
            ps.setInt(2, orderId);

            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
