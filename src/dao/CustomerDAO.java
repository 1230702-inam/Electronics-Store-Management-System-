package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import models.Customer;
import utils.DBConnection;

public class CustomerDAO {

    // Get all customers
    public ArrayList<Customer> getAllCustomers() {

        ArrayList<Customer> customers = new ArrayList<>();

        String sql = "SELECT * FROM customer";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Customer c = new Customer();
                c.setCustomerId(rs.getInt("Customer_ID"));
                c.setCustomerName(rs.getString("Customer_Name"));
                c.setEmail(rs.getString("Email"));
                c.setPhone(rs.getString("Phone_Number"));
                c.setAddress(rs.getString("Address"));

                customers.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return customers;
    }

    // Get customer by ID
    public Customer getCustomerById(int id) {

        String sql = "SELECT * FROM customer WHERE Customer_ID = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Customer c = new Customer();
                c.setCustomerId(rs.getInt("Customer_ID"));
                c.setCustomerName(rs.getString("Customer_Name"));
                c.setEmail(rs.getString("Email"));
                c.setPhone(rs.getString("Phone_Number"));
                c.setAddress(rs.getString("Address"));
                return c;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Get customer by email
    public Customer getCustomerByEmail(String email) {

        String sql = "SELECT * FROM customer WHERE Email = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Customer c = new Customer();
                c.setCustomerId(rs.getInt("Customer_ID"));
                c.setCustomerName(rs.getString("Customer_Name"));
                c.setEmail(rs.getString("Email"));
                c.setPhone(rs.getString("Phone_Number"));
                c.setAddress(rs.getString("Address"));
                return c;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Add new customer - بيرجع الـ ID الجديد
    public int addCustomer(Customer customer) {

        String sql = "INSERT INTO customer " +
                     "(Customer_Name, Email, Phone_Number, Address) " +
                     "VALUES (?, ?, ?, ?)";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                sql, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {

            ps.setString(1, customer.getCustomerName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPhone());
            ps.setString(4, customer.getAddress());

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

    // Search customers by name
    public ArrayList<Customer> searchCustomers(String keyword) {

        ArrayList<Customer> customers = new ArrayList<>();

        String sql = "SELECT * FROM customer " +
                     "WHERE Customer_Name LIKE ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Customer c = new Customer();
                c.setCustomerId(rs.getInt("Customer_ID"));
                c.setCustomerName(rs.getString("Customer_Name"));
                c.setEmail(rs.getString("Email"));
                c.setPhone(rs.getString("Phone_Number"));
                c.setAddress(rs.getString("Address"));
                customers.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return customers;
    }
}