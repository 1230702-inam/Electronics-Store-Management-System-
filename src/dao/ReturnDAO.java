package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import utils.DBConnection;

public class ReturnDAO {

    public boolean addReturn(
            int orderId,
            int productId,
            int quantity,
            String reason) {

        String sql =
                "INSERT INTO returns " +
                "(Order_ID, Product_ID, Quantity, Reason, Return_Date) " +
                "VALUES (?, ?, ?, ?, CURDATE())";

        try (
            Connection con =
                    DBConnection.getConnection();
            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {

            ps.setInt(1, orderId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            ps.setString(4, reason);

            ps.executeUpdate();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}