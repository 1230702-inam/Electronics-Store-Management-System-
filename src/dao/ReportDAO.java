package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import utils.DBConnection;

public class ReportDAO {

    // Query 1: أكثر المنتجات مبيعاً Best Selling Products
    // بنعمل JOIN بين 3 جداول: product مع order_item مع orders
    // وبنجمع الكمية المباعة من كل منتج وبنرتب تنازلياً ونوقف عند أول 10
    public LinkedHashMap<String, Integer> getBestSellingProducts() {
        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();

        String sql ="SELECT p.Product_Name, SUM(oi.Quantity) AS Total_Sold " +
                             "FROM product p " +
                            "JOIN order_item oi ON p.Product_ID = oi.Product_ID "
                             + "JOIN orders o ON oi.Order_ID = o.Order_ID " +
                            "WHERE o.Order_Status = 'Completed' " + 
                             "GROUP BY p.Product_Name " +
                            "ORDER BY Total_Sold DESC " +"LIMIT 10";
        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                result.put(rs.getString("Product_Name"), rs.getInt("Total_Sold"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // Query 2Monthly Sales Revenue)
    // بنستخدم DATE_FORMAT عشان نحول التاريخ لصيغة سنه وشهور  
    // وبنجمع كل المبيعات اللي حصلت بنفس الشهر مع بعضها
    public LinkedHashMap<String, Double> getMonthlySales() {
        LinkedHashMap<String, Double> result = new LinkedHashMap<>();

        String sql ="SELECT DATE_FORMAT(Sale_Date, '%Y-%m') AS Month, " 
                            +"SUM(Total_Sale_Amount) AS Revenue " +
        		           "FROM sales_record " 
                            + "GROUP BY Month " + 
        		           "ORDER BY Month ASC";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                result.put(rs.getString("Month"), rs.getDouble("Revenue"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // Query 3: Sales by Category
    // هون عملنا JOIN بأربع جداول مرة وحدة category, product, order_item, orders
    // عشان اقدر اوصل من الكاتيجوري للمنتج ومن المنتج للطلب اللي بيعه
    public LinkedHashMap<String, Double> getSalesByCategory() {
        LinkedHashMap<String, Double> result = new LinkedHashMap<>();

        String sql ="SELECT c.Category_Name, SUM(oi.Quantity * oi.Unit_Price) AS Total_Sales " 
                           +"FROM category c " +
                         "JOIN product p ON c.Category_ID = p.Category_ID " +
                         "JOIN order_item oi ON p.Product_ID = oi.Product_ID " +
                          "JOIN orders o ON oi.Order_ID = o.Order_ID " +
                        "WHERE o.Order_Status = 'Completed' " +
                         "GROUP BY c.Category_Name " +
                       "ORDER BY Total_Sales DESC";
            
        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                result.put(rs.getString("Category_Name"), rs.getDouble("Total_Sales"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // Query 4: (Average Order Value)
    // بنستخدم AVG() على عمود Total_Amount، وقسمت  النتيجة حسب شهر الطلب
    public LinkedHashMap<String, Double> getAvgOrderValueByMonth() {
        LinkedHashMap<String, Double> result = new LinkedHashMap<>();

        String sql =
            "SELECT DATE_FORMAT(Order_Date, '%Y-%m') AS Month, " +
            "AVG(Total_Amount) AS Avg_Value " +
            "FROM orders " +
            "WHERE Order_Status = 'Completed' " +
            "GROUP BY Month " +
            "ORDER BY Month ASC";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                result.put(rs.getString("Month"), rs.getDouble("Avg_Value"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // Query 5: (Top Customers)
    // JOIN بين customer و orders
    //وبجمع كل اللي صرفه كل زبون
    // وبرتب تنازلي وبوقف عند أول 10 زبائن
    public LinkedHashMap<String, Double> getTopCustomers() {

        LinkedHashMap<String, Double> result = new LinkedHashMap<>();

        String sql =
            "SELECT cu.Customer_Name, SUM(o.Total_Amount) AS Total_Spent " +
            "FROM customer cu " +
            "JOIN orders o ON cu.Customer_ID = o.Customer_ID " +
            "WHERE o.Order_Status = 'Completed' " +
            "GROUP BY cu.Customer_Name " +
            "ORDER BY Total_Spent DESC " +
            "LIMIT 10";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                result.put(rs.getString("Customer_Name"), rs.getDouble("Total_Spent"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // Query 6: القيمة الكلية لكل زبون (Customer Lifetime Value)
    // هون بحسب 3 أشياء مرة وحدة لكل زبون:
    // عدد طلباته (COUNT)
    //، إجمالي صرفه (SUM)،
    //ومتوسط كل طلب (AVG)
    public ArrayList<String[]> getCustomerLifetimeValue() {

        ArrayList<String[]> result = new ArrayList<>();

        String sql =
            "SELECT cu.Customer_Name, " +
            "COUNT(o.Order_ID) AS Total_Orders, " +
            "SUM(o.Total_Amount) AS Total_Spent, " +
            "AVG(o.Total_Amount) AS Avg_Order_Value " +
            "FROM customer cu " +
            "JOIN orders o ON cu.Customer_ID = o.Customer_ID " +
            "WHERE o.Order_Status = 'Completed' " +
            "GROUP BY cu.Customer_Name " +
            "ORDER BY Total_Spent DESC";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                String[] row = {
                    rs.getString("Customer_Name"),
                    rs.getString("Total_Orders"),
                    "₪" + String.format("%.2f", rs.getDouble("Total_Spent")),
                    "₪" + String.format("%.2f", rs.getDouble("Avg_Order_Value"))
                };
                result.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // Query 7: الزبائن المتكررين (Repeat Customers)
    // هون استخدمت  HAVING بدل WHERE، لأنو الفلترة عم تصير على
    // نتيجة الـ GROUP BY عدد الطلبات مش على عمود موجود أصلاً بالجدول
    // يعني بعرض بس الزبائن اللي عندهم أكثر من طلب وحدة
    public ArrayList<String[]> getRepeatCustomers() {
        ArrayList<String[]> result = new ArrayList<>();

        String sql =
            "SELECT cu.Customer_Name, cu.Email, cu.Phone_Number, " +
            "COUNT(o.Order_ID) AS Order_Count, " +
            "SUM(o.Total_Amount) AS Total_Spent " +
            "FROM customer cu " +
            "JOIN orders o ON cu.Customer_ID = o.Customer_ID " +
            "GROUP BY cu.Customer_Name, cu.Email, cu.Phone_Number " +
            "HAVING Order_Count > 1 " +
            "ORDER BY Order_Count DESC";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                String[] row = {
                    rs.getString("Customer_Name"),
                    rs.getString("Email"),
                    rs.getString("Phone_Number"),
                    rs.getString("Order_Count"),
                    "₪" + String.format("%.2f", rs.getDouble("Total_Spent"))
                };
                result.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // Query 8:  Orders Summary
    // استخدمت  CASE WHEN جوا SUM عشان نقدر نعد كل حالة Completed
    // Pending, Processing) بنفس الـ Query، بدل ما نسوي 3 queries منفصلة
    public Map<String, String> getOrdersSummary() {

        LinkedHashMap<String, String> result = new LinkedHashMap<>();

        String sql =
            "SELECT COUNT(*) AS Total_Orders, " +
            "SUM(CASE WHEN Order_Status = 'Completed' THEN 1 ELSE 0 END) AS Completed, " +
            "SUM(CASE WHEN Order_Status = 'Pending' THEN 1 ELSE 0 END) AS Pending, " +
            "SUM(CASE WHEN Order_Status = 'Processing' THEN 1 ELSE 0 END) AS Processing, " +
            "SUM(Total_Amount) AS Total_Revenue " +
            "FROM orders";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                result.put("Total Orders",  rs.getString("Total_Orders"));
                result.put("Completed",     rs.getString("Completed"));
                result.put("Pending",       rs.getString("Pending"));
                result.put("Processing",    rs.getString("Processing"));
                result.put("Total Revenue", "₪" + rs.getString("Total_Revenue"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // Query 9: معدل الإرجاع لكل منتج Return Rate by Product
    // JOIN بسيط بين product و returns، وبعد كل مرة المنتج اترجع فيها
    // مفيدة عشان نعرف أي منتج فيه مشكلة كتير زبائن بيرجعوه
    public LinkedHashMap<String, Integer> getReturnsByProduct() {

        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();

        String sql =
            "SELECT p.Product_Name, COUNT(r.Return_ID) AS Total_Returns " +
            "FROM product p " +
            "JOIN returns r ON p.Product_ID = r.Product_ID " +
            "GROUP BY p.Product_Name " +
            "ORDER BY Total_Returns DESC";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                result.put(rs.getString("Product_Name"), rs.getInt("Total_Returns"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // Query 10: توزيع طرق الدفع (Payment Method Stats)
    // بسيطة: GROUP BY على عمود Payment_Method وعد كل طريقة دفع
    // كم مرة استخدمت، بس فقط للدفعات اللي حالتها Paid
    public LinkedHashMap<String, Integer> getPaymentMethodStats() {

        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();

        String sql =
            "SELECT Payment_Method, COUNT(*) AS Total " +
            "FROM payment " +
            "WHERE Payment_Status = 'Paid' " +
            "GROUP BY Payment_Method " +
            "ORDER BY Total DESC";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                result.put(rs.getString("Payment_Method"), rs.getInt("Total"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
