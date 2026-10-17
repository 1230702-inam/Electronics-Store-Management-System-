package views;

import application.Main;
import dao.CustomerDAO;
import dao.OrderDAO;
import dao.PaymentDAO;
import dao.ProductDAO;
import dao.WarrantyDAO;
import dao.ReturnDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import models.Customer;
import models.Order;
import models.OrderItem;
import models.Product;
import models.Warranty;

import java.util.ArrayList;

public class MyOrdersView extends BorderPane {

    private CustomerDAO customerDAO = new CustomerDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();
    private ProductDAO productDAO = new ProductDAO();
    private WarrantyDAO warrantyDAO = new WarrantyDAO();
    private ReturnDAO returnDAO = new ReturnDAO();

    private TextField emailField;
    private VBox resultsBox;
    private Label statusLabel;

    public MyOrdersView() {
        getStyleClass().add("root");
        VBox main = new VBox(0,
            buildNavBar(),
            buildScrollContent()
        );
        VBox.setVgrow(main, Priority.ALWAYS);
        setCenter(main);
    }

    // NAV BAR
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

    // SCROLL CONTENT
    private ScrollPane buildScrollContent() {

        StackPane header = new StackPane();
        header.getStyleClass().add("page-header");
        header.setMinHeight(110);

        Label title = new Label("My Order History");
        title.getStyleClass().add("page-header-title");
        Label sub = new Label("Enter your email to view your past orders");
        sub.getStyleClass().add("page-header-sub");
        VBox headerBox = new VBox(8, title, sub);
        headerBox.setPadding(new Insets(0, 0, 0, 40));
        StackPane.setAlignment(headerBox, Pos.CENTER_LEFT);
        header.getChildren().add(headerBox);

        VBox lookupCard = buildLookupCard();

        resultsBox = new VBox(16);

        VBox content = new VBox(0, header,
            wrapWithPadding(lookupCard),
            wrapWithPadding(resultsBox)
        );

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("transparent-scroll");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return scroll;
    }

    private VBox wrapWithPadding(javafx.scene.Node node) {
        VBox wrapper = new VBox(node);
        wrapper.setPadding(new Insets(20, 40, 0, 40));
        wrapper.getStyleClass().add("root");
        return wrapper;
    }

    // EMAIL LOOKUP CARD
    private VBox buildLookupCard() {

        Label label = new Label("EMAIL ADDRESS");
        label.getStyleClass().add("form-label");

        emailField = new TextField();
        emailField.setPromptText("Enter the email you used at checkout");
        emailField.setPrefHeight(40);
        emailField.getStyleClass().add("form-field");
        emailField.setOnAction(e -> lookupOrders());

        Button searchBtn = new Button("View Orders");
        searchBtn.getStyleClass().add("btn-primary");
        searchBtn.setOnAction(e -> lookupOrders());

        HBox row = new HBox(12, emailField, searchBtn);
        HBox.setHgrow(emailField, Priority.ALWAYS);
        row.setAlignment(Pos.CENTER_LEFT);

        statusLabel = new Label();
        statusLabel.getStyleClass().add("section-sub");

        VBox card = new VBox(8, label, row, statusLabel);
        card.getStyleClass().add("card-white");
        card.setPadding(new Insets(24));

        return card;
    }

    private void lookupOrders() {
        resultsBox.getChildren().clear();
        statusLabel.setText("");

        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            statusLabel.setText("Please enter your email address.");
            return;
        }

        Customer customer = customerDAO.getCustomerByEmail(email);
        if (customer == null) {
            statusLabel.setText("No customer found with this email.");
            return;
        }

        ArrayList<Order> orders = orderDAO.getOrdersByCustomer(customer.getCustomerId());
        if (orders.isEmpty()) {
            statusLabel.setText("No orders found for this account yet.");
            return;
        }

        statusLabel.setText("Welcome back, " + customer.getCustomerName() + ". Showing " + orders.size() + " order(s).");

        for (Order order : orders) {
            resultsBox.getChildren().add(buildOrderCard(order));
        }
    }

    // ORDER CARD
    private VBox buildOrderCard(Order order) {
        Label orderIdLabel = new Label("Order #" + order.getOrderId());
        orderIdLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #1B2230;");

        Label dateLabel = new Label(order.getOrderDate() != null ? order.getOrderDate().toString() : "");
        dateLabel.getStyleClass().add("product-brand");

        VBox titleBox = new VBox(4, orderIdLabel, dateLabel);

        Label statusBadge = new Label(order.getStatus() != null ? order.getStatus() : "Pending");
        statusBadge.setStyle(statusStyle(order.getStatus()));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label totalLabel = new Label("₪ " + String.format("%.2f", order.getTotalAmount()));
        totalLabel.getStyleClass().add("price-text");

        HBox topRow = new HBox(16, titleBox, spacer, statusBadge, totalLabel);
        topRow.setAlignment(Pos.CENTER_LEFT);

        // Order items
        VBox itemsBox = new VBox(8);
        ArrayList<OrderItem> items = orderDAO.getOrderItems(order.getOrderId());
        for (OrderItem item : items) {
            Product p = productDAO.getProductById(item.getProductId());
            String name = p != null ? p.getProductName() : "Product #" + item.getProductId();
            Label itemLabel = new Label(name + "  x" + item.getQuantity());
            itemLabel.setStyle("-fx-font-size: 12.5px; -fx-text-fill: #1B2230;");

            Label itemPriceLabel = new Label("₪ " + String.format("%.2f", item.getUnitPrice() * item.getQuantity()));
            itemPriceLabel.setStyle("-fx-font-size: 12.5px; -fx-font-weight: 700; -fx-text-fill: #6B7280;");

            Region itemSpacer = new Region();
            HBox.setHgrow(itemSpacer, Priority.ALWAYS);
            HBox itemRow = new HBox(itemLabel, itemSpacer, itemPriceLabel);
            itemRow.setAlignment(Pos.CENTER_LEFT);
            itemsBox.getChildren().add(itemRow);
        }

        Region divider = new Region();
        divider.setPrefHeight(1);
        divider.getStyleClass().add("thin-divider");

        Button warrantyBtn = new Button("View Warranty");
        warrantyBtn.getStyleClass().add("btn-secondary");
        warrantyBtn.setOnAction(e -> showWarranty(order.getOrderId()));

        Button returnBtn = new Button("Request Return");
        returnBtn.getStyleClass().add("btn-secondary");
        returnBtn.setOnAction(e -> handleReturn(order.getOrderId()));

        HBox buttons = new HBox(10, warrantyBtn, returnBtn);

        VBox card = new VBox(14, topRow, divider, itemsBox, buttons);
        card.getStyleClass().add("card-white");
        card.setPadding(new Insets(20));

        return card;
    }

    private String statusStyle(String status) {
        if (status == null) status = "Pending";
        switch (status) {
            case "Completed":
                return "-fx-background-color: rgba(30,127,75,0.10); -fx-text-fill: #1E7F4B; " +
                       "-fx-font-size: 11px; -fx-font-weight: 700; -fx-background-radius: 10px; -fx-padding: 4 10 4 10;";
            case "Processing":
                return "-fx-background-color: rgba(30,127,123,0.10); -fx-text-fill: #1E7F7B; " +
                       "-fx-font-size: 11px; -fx-font-weight: 700; -fx-background-radius: 10px; -fx-padding: 4 10 4 10;";
            default:
                return "-fx-background-color: rgba(214,158,46,0.12); -fx-text-fill: #B7791F; " +
                       "-fx-font-size: 11px; -fx-font-weight: 700; -fx-background-radius: 10px; -fx-padding: 4 10 4 10;";
        }
    }

    // WARRANTY INFO
    private void showWarranty(int orderId) {

        ArrayList<OrderItem> items = orderDAO.getOrderItems(orderId);
        StringBuilder text = new StringBuilder();

        for (OrderItem item : items) {

            Product p = productDAO.getProductById(item.getProductId());
            Warranty w = warrantyDAO.getWarrantyByProduct(item.getProductId());

            text.append(p.getProductName());
            text.append("\n");
            if (w == null) {
                text.append("No warranty\n\n");
            } else {

                java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
                String status = today.after(w.getEndDate()) ? "Expired" : "Active";
                text.append("Start: ").append(w.getStartDate()).append("\n");
                text.append("End: ").append(w.getEndDate()).append("\n");
                text.append("Status: ").append(status).append("\n\n");
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Warranty");
        alert.setHeaderText("Warranty Information");
        alert.setContentText(text.toString());
        alert.showAndWait();
    }

    // RETURN A PRODUCT (with real stock restock)
    private void handleReturn(int orderId) {

        ArrayList<OrderItem> items = orderDAO.getOrderItems(orderId);

        ChoiceDialog<String> dialog = new ChoiceDialog<>();
        ArrayList<Product> products = new ArrayList<>();
        ArrayList<Integer> quantities = new ArrayList<>();

        for (OrderItem item : items) {
            Product p = productDAO.getProductById(item.getProductId());
            if (p != null) {
                products.add(p);
                quantities.add(item.getQuantity());
                dialog.getItems().add(p.getProductName());
            }
        }

        if (dialog.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("No products found.");
            alert.showAndWait();
            return;
        }

        dialog.setTitle("Return");
        dialog.setHeaderText("Select a product to return");

        dialog.showAndWait().ifPresent(name -> {

            Product selected = null;
            int orderedQty = 1;

            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).getProductName().equals(name)) {
                    selected = products.get(i);
                    orderedQty = quantities.get(i);
                    break;
                }
            }

            if (selected == null) return;
            final Product product = selected;
            final int returnQty = orderedQty;

            Warranty w = warrantyDAO.getWarrantyByProduct(selected.getProductId());

            if (w == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText(null);
                alert.setContentText("This product has no warranty.");
                alert.showAndWait();
                return;
            }

            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

            if (today.after(w.getEndDate())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText(null);
                alert.setContentText("Warranty expired. Return is not available.");
                alert.showAndWait();
                return;
            }

            TextInputDialog reasonDialog = new TextInputDialog();
            reasonDialog.setTitle("Return");
            reasonDialog.setHeaderText("Enter return reason");

            reasonDialog.showAndWait().ifPresent(reason -> {

                boolean saved = returnDAO.addReturn(orderId, product.getProductId(), returnQty, reason);

                if (!saved) {
                    Alert fail = new Alert(Alert.AlertType.ERROR);
                    fail.setHeaderText(null);
                    fail.setContentText("Could not submit the return. Please try again.");
                    fail.showAndWait();
                    return;
                }

             
                productDAO.increaseStock(product.getProductId(), returnQty);

                Alert ok = new Alert(Alert.AlertType.INFORMATION);
                ok.setHeaderText(null);
                ok.setContentText("Return request submitted. " + returnQty +
                    " unit(s) of \"" + product.getProductName() + "\" returned to stock.");
                ok.showAndWait();
            });
        });
    }
}
