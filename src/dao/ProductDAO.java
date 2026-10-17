package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import models.Product;
import utils.DBConnection;

public class ProductDAO {

    //  عشان منكررش نفس كود القراءة بكل method
    private Product mapProduct(ResultSet rs) throws Exception {
        Product p = new Product();
        p.setProductId(rs.getInt("Product_ID"));
        p.setProductName(rs.getString("Product_Name"));
        p.setBrand(rs.getString("Brand"));
        p.setModel(rs.getString("Model"));
        p.setPrice(rs.getDouble("Price"));
        p.setImagePath(rs.getString("Image_Path"));
        p.setQuantity(rs.getInt("Quantity"));
        p.setCategoryId(rs.getInt("Category_ID"));
        return p;
    }

    // رجعلي كل المنتجات
    public ArrayList<Product> getAllProducts() {

        ArrayList<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCT ORDER BY Product_Name ASC";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) products.add(mapProduct(rs));
        } catch (Exception e) { e.printStackTrace(); }

        return products;
    }

    // رجعلي منتج وحدة بالـ ID تبعو
    public Product getProductById(int productId) {

        String sql = "SELECT * FROM PRODUCT WHERE Product_ID = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapProduct(rs);
        } catch (Exception e) { e.printStackTrace(); }

        return null;
    }

    // بحث بالاسم أو البراند
    public ArrayList<Product> searchProducts(String keyword) {

        ArrayList<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCT WHERE Product_Name LIKE ? OR Brand LIKE ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) products.add(mapProduct(rs));
        } catch (Exception e) { e.printStackTrace(); }

        return products;
    }

    // رجعلي كل منتجات كاتيجوري معينة
    public ArrayList<Product> getProductsByCategory(int categoryId) {

        ArrayList<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCT WHERE Category_ID = ? ORDER BY Product_Name ASC";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) products.add(mapProduct(rs));
        } catch (Exception e) { e.printStackTrace(); }

        return products;
    }

    // رجعلي كل الكاتيجوريز 
    public ArrayList<String[]> getAllCategories() {

        ArrayList<String[]> categories = new ArrayList<>();
        String sql = "SELECT * FROM CATEGORY ORDER BY Category_ID ASC";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                String[] cat = {
                    rs.getString("Category_ID"),
                    rs.getString("Category_Name"),
                    rs.getString("Category_Description"),
                    rs.getString("Image_Path")
                };
                categories.add(cat);
            }
        } catch (Exception e) { e.printStackTrace(); }

        return categories;
    }

    // بننزل الكمية لما يصير طلب شراء، وبس إذا الكمية المتوفرة كافية
    public boolean decreaseStock(int productId, int qty) {

        String sql = "UPDATE PRODUCT SET Quantity = Quantity - ? " +
                     "WHERE Product_ID = ? AND Quantity >= ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, qty);
            ps.setInt(2, productId);
            ps.setInt(3, qty);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // برجع الكمية للمخزون لما يصير Return (Restock)
    public boolean increaseStock(int productId, int qty) {

        String sql = "UPDATE PRODUCT SET Quantity = Quantity + ? WHERE Product_ID = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, qty);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // بشيك الكمية المتوفرة حالياً، قبل ما نسمح بإضافة للسلة أو الشراء
    public int getStockQuantity(int productId) {

        String sql = "SELECT Quantity FROM PRODUCT WHERE Product_ID = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("Quantity");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}
