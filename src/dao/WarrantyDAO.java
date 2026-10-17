package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import models.Warranty;
import utils.DBConnection;

public class WarrantyDAO {

    public Warranty getWarrantyByProduct(int productId) {

        String sql =
                "SELECT * FROM warranty " +
                "WHERE Product_ID = ?";

        try (
            Connection con =
                    DBConnection.getConnection();
            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {

            ps.setInt(1, productId);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                Warranty w =
                        new Warranty();

                w.setWarrantyId(
                        rs.getInt("Warranty_ID"));

                w.setProductId(
                        rs.getInt("Product_ID"));

                w.setStartDate(
                        rs.getDate("Start_Date"));

                w.setEndDate(
                        rs.getDate("End_Date"));

                return w;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}