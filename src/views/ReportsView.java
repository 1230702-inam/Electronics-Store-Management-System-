package views;

import application.Main;
import dao.ReportDAO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReportsView extends BorderPane {
    private ReportDAO reportDAO = new ReportDAO();

    private VBox contentArea;   // المنطقة اللي بيتغير فيها محتوى التقرير
    private VBox sidebarBox;    // المكان الي فيه اسماء التقارير 

    private String activeReportId = "best_sellers";

    private static final String[][] SALES_REPORTS = {
        {"best_sellers",       "Best Selling Products"},
        {"monthly_sales",      "Monthly Sales Revenue"},
        {"sales_by_category",  "Sales by Category"},
        {"avg_order_value",    "Average Order Value"}
    };

    private static final String[][] CUSTOMER_REPORTS = {
        {"top_customers",      "Top Customers"},
        {"customer_ltv",       "Customer Lifetime Value"},
        {"repeat_customers",   "Repeat Customers"}
    };

    private static final String[][] ORDER_REPORTS = {
        {"orders_summary",     "Orders Summary"},
        {"returns_by_product", "Return Rate by Product"},
        {"payment_methods",    "Payment Method Stats"}
    };

    public ReportsView() {
        getStyleClass().add("root");

        VBox main = new VBox(0,
            buildNavBar(),
            buildBody()
        );
        VBox.setVgrow(main, Priority.ALWAYS);
        setCenter(main);
    }

    private HBox buildNavBar() {

        Circle logoCircle = new Circle(19);
        logoCircle.setFill(Color.web("#1E7F7B"));
        Label logoLetter = new Label("Z");
        logoLetter.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: 800;");
        StackPane logoIcon = new StackPane(logoCircle, logoLetter);

        Label logoText = new Label("ZainTech");
        logoText.getStyleClass().add("navbar-logo-text");
        Label logoSub = new Label("ELECTRONICS STORE");
        logoSub.getStyleClass().add("navbar-logo-sub");
        VBox logoWords = new VBox(1, logoText, logoSub);
        logoWords.setAlignment(Pos.CENTER_LEFT);

        HBox logo = new HBox(10, logoIcon, logoWords);
        logo.setAlignment(Pos.CENTER_LEFT);
        logo.setCursor(Cursor.HAND);
        logo.setOnMouseClicked(e -> Main.setView(new DashboardView()));

        Button homeBtn = navBtn("Home", e -> Main.setView(new DashboardView()));
        Button shopBtn = navBtn("Shop", e -> Main.setView(new ProductsView(0)));
        Button cartBtn = navBtn("Cart", e -> Main.setView(new OrderView()));

        HBox navLinks = new HBox(6, homeBtn, shopBtn, cartBtn);
        navLinks.setAlignment(Pos.CENTER);

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        HBox navbar = new HBox(16, logo, spacer1, navLinks);
        navbar.getStyleClass().add("navbar");
        navbar.setAlignment(Pos.CENTER);
        navbar.setPadding(new Insets(14, 36, 14, 36));
        navbar.setMinHeight(64);

        return navbar;
    }

    private Button navBtn(String text, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button btn = new Button(text);
        btn.getStyleClass().add("navbar-btn");
        btn.setOnAction(action);
        return btn;
    }

   private HBox buildBody() {
        VBox sidebar = buildSidebar();
        ScrollPane contentScroll = buildContentArea();

        HBox body = new HBox(sidebar, contentScroll);
        HBox.setHgrow(contentScroll, Priority.ALWAYS);
        VBox.setVgrow(body, Priority.ALWAYS);
        return body;
    }

    private VBox buildSidebar() {

        sidebarBox = new VBox(20);
        sidebarBox.setPadding(new Insets(24, 16, 24, 16));
        sidebarBox.setPrefWidth(240);
        sidebarBox.getStyleClass().add("reports-sidebar");

        // كل مرة بنبني محتوى السايدبار من جديد عشان نقدر نلون
        // الزر النشط (active) ونوضحه عن باقي الأزرار
        sidebarBox.getChildren().setAll(buildSidebarContent());

        ScrollPane scroll = new ScrollPane(sidebarBox);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("transparent-scroll");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setPrefWidth(240);

        VBox wrapper = new VBox(scroll);
        wrapper.getStyleClass().add("reports-sidebar");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        return wrapper;
    }

    private ArrayList<javafx.scene.Node> buildSidebarContent() {
        ArrayList<javafx.scene.Node> nodes = new ArrayList<>();

        Label header = new Label("Reports");
        header.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: white;");
        nodes.add(header);

        nodes.add(sidebarGroup("SALES", SALES_REPORTS));
        nodes.add(sidebarGroup("CUSTOMERS", CUSTOMER_REPORTS));
        nodes.add(sidebarGroup("ORDERS", ORDER_REPORTS));

        return nodes;
    }

    private VBox sidebarGroup(String groupTitle, String[][] items) {
        Label groupLabel = new Label(groupTitle);
        groupLabel.getStyleClass().add("reports-sidebar-title");

        VBox group = new VBox(4);
        group.getChildren().add(groupLabel);

        for (String[] item : items) {

            String reportId = item[0];
            String reportName = item[1];

            Button btn = new Button(reportName);
            btn.setMaxWidth(Double.MAX_VALUE);
            boolean isActive = reportId.equals(activeReportId);
            btn.getStyleClass().add(isActive ? "report-nav-btn-active" : "report-nav-btn");

            btn.setOnAction(e -> selectReport(reportId));
            group.getChildren().add(btn);
        }

        return new VBox(8, group);
    }

    private void selectReport(String reportId) {
        activeReportId = reportId;
        sidebarBox.getChildren().setAll(buildSidebarContent());
        renderReport(reportId);
    }

    private ScrollPane buildContentArea() {

        contentArea = new VBox(20);
        contentArea.setPadding(new Insets(32, 40, 40, 40));
        contentArea.getStyleClass().add("root");

        renderReport(activeReportId);

        ScrollPane scroll = new ScrollPane(contentArea);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("transparent-scroll");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scroll;
    }

    // عنوان وعنوان فرعي لكل تقرير
    private VBox sectionHeader(String title, String sub) {
        Label t = new Label(title);
        t.getStyleClass().add("report-content-title");
        Label s = new Label(sub);
        s.getStyleClass().add("report-content-sub");
        return new VBox(4, t, s);
    }

    
    private void renderReport(String reportId) {
        contentArea.getChildren().clear();

        switch (reportId) {
            case "best_sellers":        renderBestSellers();        break;
            case "monthly_sales":       renderMonthlySales();       break;
            case "sales_by_category":   renderSalesByCategory();    break;
            case "avg_order_value":     renderAvgOrderValue();      break;
            case "top_customers":       renderTopCustomers();       break;
            case "customer_ltv":        renderCustomerLtv();        break;
            case "repeat_customers":    renderRepeatCustomers();    break;
            case "orders_summary":      renderOrdersSummary();      break;
            case "returns_by_product":  renderReturnsByProduct();   break;
            case "payment_methods":     renderPaymentMethods();     break;
            default:                    renderBestSellers();        break;
        }
    }

    private VBox buildBarChart(Map<String, ? extends Number> data, String unitPrefix) {

        double max = 0;
        for (Number n : data.values()) {
            if (n.doubleValue() > max) {
                max = n.doubleValue();
            }
        }
        if (max == 0) max = 1; // حماية من القسمة على صفر

        HBox barsRow = new HBox(20);
        barsRow.setAlignment(Pos.BOTTOM_LEFT);
        barsRow.setPadding(new Insets(20, 10, 10, 10));

        double maxBarHeight = 200; 

        for (Map.Entry<String, ? extends Number> entry : data.entrySet()) {

            double value = entry.getValue().doubleValue();
            double height = Math.max(6, (value / max) * maxBarHeight);
            Label valueLabel = new Label(unitPrefix + formatNumber(value));
            valueLabel.getStyleClass().add("chart-bar-value");

            Rectangle bar = new Rectangle(44, height);
            bar.getStyleClass().add("chart-bar");

            Label nameLabel = new Label(entry.getKey());
            nameLabel.getStyleClass().add("chart-bar-label");
            nameLabel.setWrapText(true);
            nameLabel.setMaxWidth(70);
            nameLabel.setAlignment(Pos.CENTER);

            VBox barCol = new VBox(6, valueLabel, bar, nameLabel);
            barCol.setAlignment(Pos.BOTTOM_CENTER);
            barsRow.getChildren().add(barCol);
        }

        ScrollPane scroll = new ScrollPane(barsRow);
        scroll.setFitToHeight(true);
        scroll.getStyleClass().add("transparent-scroll");
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setPrefHeight(280);

        VBox chartCard = new VBox(scroll);
        chartCard.getStyleClass().add("card-white");
        chartCard.setPadding(new Insets(20));

        return chartCard;
    }

    private String formatNumber(double value) {
        if (value == Math.floor(value)) {
            return String.format("%.0f", value);
        }
        return String.format("%.2f", value);
    }

    private TableView<String[]> buildTable(String[] columns, ArrayList<String[]> rows) {

        TableView<String[]> table = new TableView<>();
        table.getStyleClass().add("report-table");
        table.setItems(FXCollections.observableArrayList(rows));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        for (int i = 0; i < columns.length; i++) {

            final int colIndex = i; 
            TableColumn<String[], String> col = new TableColumn<>(columns[i]);
            col.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                    colIndex < data.getValue().length ? data.getValue()[colIndex] : ""));

            table.getColumns().add(col);
        }

        table.setPrefHeight(Math.min(400, rows.size() * 36 + 40));
        return table;
    }

    private HBox kpiCard(String label, String value) {

        Label valueLbl = new Label(value);
        valueLbl.getStyleClass().add("kpi-value");

        Label labelLbl = new Label(label);
        labelLbl.getStyleClass().add("kpi-label");

        VBox textBox = new VBox(2, valueLbl, labelLbl);

        HBox card = new HBox(textBox);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("kpi-card");
        card.setPadding(new Insets(18));
        card.setPrefWidth(200);
        return card;
    }

    private Label emptyMessage(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("empty-state-label");
        return lbl;
    }

    // (Best Selling Products)
    //  JOIN بين product و order_item و orders
    private void renderBestSellers() {

        contentArea.getChildren().add(
            sectionHeader("Best Selling Products", "Top 10 products by quantity sold"));

        LinkedHashMap<String, Integer> data = reportDAO.getBestSellingProducts();

        if (data.isEmpty()) {
            contentArea.getChildren().add(emptyMessage("No sales data available."));
            return;
        }

        contentArea.getChildren().add(buildBarChart(data, ""));
        ArrayList<String[]> rows = new ArrayList<>();
        for (Map.Entry<String, Integer> e : data.entrySet()) {
            rows.add(new String[]{ e.getKey(), String.valueOf(e.getValue()) });
        }
        contentArea.getChildren().add(
            buildTable(new String[]{ "Product Name", "Units Sold" }, rows));
    }

    // (Monthly Sales Revenue)
    private void renderMonthlySales() {

        contentArea.getChildren().add(
            sectionHeader("Monthly Sales Revenue", "Total revenue generated per month"));
        LinkedHashMap<String, Double> data = reportDAO.getMonthlySales();
        if (data.isEmpty()) {
            contentArea.getChildren().add(emptyMessage("No sales data available."));
            return;
        }

        contentArea.getChildren().add(buildBarChart(data, "₪"));
        ArrayList<String[]> rows = new ArrayList<>();
        for (Map.Entry<String, Double> e : data.entrySet()) {
            rows.add(new String[]{ e.getKey(), "₪ " + String.format("%.2f", e.getValue()) });
        }
        contentArea.getChildren().add(buildTable(new String[]{ "Month", "Revenue" }, rows));
    }

// (Sales by Category)
    private void renderSalesByCategory() {
        contentArea.getChildren().add(
            sectionHeader("Sales by Category", "Revenue breakdown across product categories"));
        LinkedHashMap<String, Double> data = reportDAO.getSalesByCategory();

        if (data.isEmpty()) {
            contentArea.getChildren().add(emptyMessage("No sales data available."));
            return;
        }
        contentArea.getChildren().add(buildBarChart(data, "₪"));
        ArrayList<String[]> rows = new ArrayList<>();
        for (Map.Entry<String, Double> e : data.entrySet()) {
            rows.add(new String[]{ e.getKey(), "₪ " + String.format("%.2f", e.getValue()) });
        }
        contentArea.getChildren().add(buildTable(new String[]{ "Category", "Total Sales" }, rows));
    }

    // (Average Order Value)
    private void renderAvgOrderValue() {
        contentArea.getChildren().add(
            sectionHeader("Average Order Value", "Average amount spent per order, by month"));

        LinkedHashMap<String, Double> data = reportDAO.getAvgOrderValueByMonth();

        if (data.isEmpty()) {
            contentArea.getChildren().add(emptyMessage("No data available."));
            return;
        }
        contentArea.getChildren().add(buildBarChart(data, "₪"));

        ArrayList<String[]> rows = new ArrayList<>();
        for (Map.Entry<String, Double> e : data.entrySet()) {
            rows.add(new String[]{ e.getKey(), "₪ " + String.format("%.2f", e.getValue()) });
        }
        contentArea.getChildren().add(buildTable(new String[]{ "Month", "Avg Order Value" }, rows));
    }

    // (Top Customers)
    private void renderTopCustomers() {
        contentArea.getChildren().add(
            sectionHeader("Top Customers", "Customers ranked by total spending"));

        LinkedHashMap<String, Double> data = reportDAO.getTopCustomers();

        if (data.isEmpty()) {
            contentArea.getChildren().add(emptyMessage("No data available."));
            return;
        }
        contentArea.getChildren().add(buildBarChart(data, "₪"));
        ArrayList<String[]> rows = new ArrayList<>();
        for (Map.Entry<String, Double> e : data.entrySet()) {
            rows.add(new String[]{ e.getKey(), "₪ " + String.format("%.2f", e.getValue()) });
        }

        contentArea.getChildren().add(buildTable(new String[]{ "Customer Name", "Total Spent" }, rows));
    }

    // (Customer Lifetime Value)
    // عدد طلباته + إجمالي صرفه + متوسط كل طلب
    private void renderCustomerLtv() {
        contentArea.getChildren().add(
            sectionHeader("Customer Lifetime Value",
                "Total orders, spending, and average order value per customer"));

        ArrayList<String[]> rows = reportDAO.getCustomerLifetimeValue();

        if (rows.isEmpty()) {
            contentArea.getChildren().add(emptyMessage("No data available."));
            return;
        }

        contentArea.getChildren().add(
            buildTable(new String[]{ "Customer", "Total Orders", "Total Spent", "Avg Order Value" }, rows));
    }


    // (Repeat Customers)    
    private void renderRepeatCustomers() {

        contentArea.getChildren().add(
            sectionHeader("Repeat Customers", "Customers who placed more than one order"));

        ArrayList<String[]> rows = reportDAO.getRepeatCustomers();

        if (rows.isEmpty()) {
            contentArea.getChildren().add(emptyMessage("No repeat customers yet."));
            return;
        }

        contentArea.getChildren().add(
            buildTable(new String[]{ "Customer", "Email", "Phone", "Order Count", "Total Spent" }, rows));
    }

    // (Orders Summary)
    private void renderOrdersSummary() {

        contentArea.getChildren().add(
            sectionHeader("Orders Summary", "Quick overview of order status distribution"));

        Map<String, String> data = reportDAO.getOrdersSummary();

        if (data.isEmpty()) {
            contentArea.getChildren().add(emptyMessage("No orders yet."));
            return;
        }

        HBox kpiRow = new HBox(16);
        kpiRow.setAlignment(Pos.CENTER_LEFT);

        for (Map.Entry<String, String> e : data.entrySet()) {
            kpiRow.getChildren().add(kpiCard(e.getKey(), e.getValue()));
        }

        ScrollPane scroll = new ScrollPane(kpiRow);
        scroll.setFitToHeight(true);
        scroll.getStyleClass().add("transparent-scroll");
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setPrefHeight(110);

        contentArea.getChildren().add(scroll);
    }

    
    // (Return Rate by Product)
    private void renderReturnsByProduct() {
        contentArea.getChildren().add(
            sectionHeader("Return Rate by Product", "Number of returns recorded per product"));
        LinkedHashMap<String, Integer> data = reportDAO.getReturnsByProduct();
        if (data.isEmpty()) {
            contentArea.getChildren().add(emptyMessage("No returns recorded."));
            return;
        }

        contentArea.getChildren().add(buildBarChart(data, ""));

        ArrayList<String[]> rows = new ArrayList<>();
        for (Map.Entry<String, Integer> e : data.entrySet()) {
            rows.add(new String[]{ e.getKey(), String.valueOf(e.getValue()) });
        }

        contentArea.getChildren().add(buildTable(new String[]{ "Product", "Total Returns" }, rows));
    }

    //   (Payment Method Stats)
    private void renderPaymentMethods() {
        contentArea.getChildren().add(
            sectionHeader("Payment Method Distribution", "How customers prefer to pay"));

        LinkedHashMap<String, Integer> data = reportDAO.getPaymentMethodStats();

        if (data.isEmpty()) {
            contentArea.getChildren().add(emptyMessage("No payment data available."));
            return;
        }
        contentArea.getChildren().add(buildBarChart(data, ""));

        ArrayList<String[]> rows = new ArrayList<>();
        for (Map.Entry<String, Integer> e : data.entrySet()) {
            rows.add(new String[]{ e.getKey(), String.valueOf(e.getValue()) });
        }
        contentArea.getChildren().add(buildTable(new String[]{ "Payment Method", "Times Used" }, rows));
    }
}
