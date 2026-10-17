package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import models.Payment;
import utils.DBConnection;

public class PaymentDAO {

    // Get all payments
    public ArrayList<Payment> getAllPayments() {

        ArrayList<Payment> payments = new ArrayList<>();

        String sql = "SELECT * FROM payment ORDER BY Payment_Date DESC";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Payment payment = new Payment();
                payment.setPaymentId(rs.getInt("Payment_ID"));
                payment.setOrderId(rs.getInt("Order_ID"));
                payment.setAmount(rs.getDouble("Amount"));
                payment.setPaymentMethod(rs.getString("Payment_Method"));
                payment.setPaymentDate(rs.getDate("Payment_Date"));

                payments.add(payment);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return payments;
    }

    // Get payment by order ID
    public Payment getPaymentByOrder(int orderId) {

        String sql = "SELECT * FROM payment WHERE Order_ID = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Payment payment = new Payment();
                payment.setPaymentId(rs.getInt("Payment_ID"));
                payment.setOrderId(rs.getInt("Order_ID"));
                payment.setAmount(rs.getDouble("Amount"));
                payment.setPaymentMethod(rs.getString("Payment_Method"));
                payment.setPaymentDate(rs.getDate("Payment_Date"));
                return payment;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Add payment + Sales Record تلقائياً
    public boolean addPayment(int orderId,
                              String paymentMethod,
                              double amount) {

        String sqlPayment =
                "INSERT INTO payment " +
                "(Order_ID, Payment_Method, Payment_Date, Amount, Payment_Status) " +
                "VALUES (?, ?, CURDATE(), ?, 'Paid')";

        String sqlSales =
                "INSERT INTO sales_record " +
                "(Order_ID, Sale_Date, Total_Sale_Amount) " +
                "VALUES (?, CURDATE(), ?)";

        String sqlOrder =
                "UPDATE orders SET Order_Status = 'Completed' " +
                "WHERE Order_ID = ?";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            // 1. Payment
            PreparedStatement ps1 =
                    con.prepareStatement(sqlPayment);
            ps1.setInt(1, orderId);
            ps1.setString(2, paymentMethod);
            ps1.setDouble(3, amount);
            ps1.executeUpdate();

            // 2. Sales Record
            PreparedStatement ps2 =
                    con.prepareStatement(sqlSales);
            ps2.setInt(1, orderId);
            ps2.setDouble(2, amount);
            ps2.executeUpdate();

            // 3. Update Order Status
            PreparedStatement ps3 =
                    con.prepareStatement(sqlOrder);
            ps3.setInt(1, orderId);
            ps3.executeUpdate();

            con.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Get total revenue
    public double getTotalRevenue() {

        String sql = "SELECT SUM(Amount) AS Total " +
                     "FROM payment " +
                     "WHERE Payment_Status = 'Paid'";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getDouble("Total");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}
