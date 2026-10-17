package views;

import application.Main;
import dao.ProductDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import models.Product;
import utils.CartManager;
import utils.CategoryImages;

import java.util.LinkedHashMap;
import java.util.Map;

public class OrderView extends BorderPane {

    private ProductDAO productDAO = new ProductDAO();

    private VBox cartItemsBox;
    private Label subtotalLabel, shippingLabel, totalLabel;
    private VBox rightPanel;

    public OrderView() {
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
        Label logoSub = new Label("ELECTRONIC STORE");
        logoSub.getStyleClass().add("navbar-logo-sub");
        VBox logoWords = new VBox(1, logoText, logoSub);
        logoWords.setAlignment(Pos.CENTER_LEFT);

        HBox logo = new HBox(10, logoIcon, logoWords);
        logo.setAlignment(Pos.CENTER_LEFT);
        logo.setCursor(Cursor.HAND);
        logo.setOnMouseClicked(e -> Main.setView(new DashboardView()));

        Button homeBtn   = navBtn("Home",      e -> Main.setView(new DashboardView()));
        Button shopBtn   = navBtn("Shop",      e -> Main.setView(new ProductsView(0)));
        Button ordersBtn = navBtn("My Orders", e -> Main.setView(new MyOrdersView()));

        HBox navLinks = new HBox(6, homeBtn, shopBtn, ordersBtn);
        navLinks.setAlignment(Pos.CENTER);

        Button reportsBtn = new Button("Reports");
        reportsBtn.getStyleClass().add("reports-btn");
        reportsBtn.setOnAction(e -> Main.setView(new ReportsView()));

        HBox rightIcons = new HBox(12, reportsBtn);
        rightIcons.setAlignment(Pos.CENTER_RIGHT);

        Region spacer1 = new Region();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        HBox navbar = new HBox(16, logo, spacer1, navLinks, spacer2, rightIcons);
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

        Label title = new Label("Shopping Cart");
        title.getStyleClass().add("page-header-title");
        Label sub = new Label("Review your items before checkout");
        sub.getStyleClass().add("page-header-sub");
        VBox headerBox = new VBox(8, title, sub);
        headerBox.setPadding(new Insets(0, 0, 0, 40));
        StackPane.setAlignment(headerBox, Pos.CENTER_LEFT);
        header.getChildren().add(headerBox);

        HBox bodyRow = buildCartBody();

        VBox content = new VBox(0, header, bodyRow);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("transparent-scroll");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return scroll;
    }

    // CART BODY (items list + summary panel)
    private HBox buildCartBody() {
        cartItemsBox = new VBox(16);

        VBox leftCol = new VBox(16, cartItemsBox);
        leftCol.setPrefWidth(680);

        rightPanel = buildSummaryPanel();

        refreshCartItems();

        HBox row = new HBox(28, leftCol, rightPanel);
        row.setPadding(new Insets(32, 40, 44, 40));
        row.setAlignment(Pos.TOP_LEFT);

        return row;
    }

    private void refreshCartItems() {
        cartItemsBox.getChildren().clear();

        LinkedHashMap<Product, Integer> cart = CartManager.getCart();

        if (cart.isEmpty()) {
            VBox emptyBox = new VBox(14);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(60, 20, 60, 20));
            emptyBox.getStyleClass().add("card-white");

            Label msg = new Label("Your cart is empty");
            msg.setStyle("-fx-font-size: 16px; -fx-font-weight: 700; -fx-text-fill: #1B2230;");
            Label hint = new Label("Browse our products and add items to get started.");
            hint.getStyleClass().add("section-sub");

            Button shopBtn = new Button("Start Shopping");
            shopBtn.getStyleClass().add("btn-primary");
            shopBtn.setOnAction(e -> Main.setView(new ProductsView(0)));

            emptyBox.getChildren().addAll(msg, hint, shopBtn);
            cartItemsBox.getChildren().add(emptyBox);
            updateSummary();
            return;
        }

        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            cartItemsBox.getChildren().add(buildCartRow(entry.getKey(), entry.getValue()));
        }
        updateSummary();
    }

    private HBox buildCartRow(Product p, int qty) {

        StackPane imageBox = new StackPane();
        imageBox.setPrefSize(96, 96);
        imageBox.getStyleClass().add("image-box");

        //من الداتا بيس 
        Image img = CategoryImages.loadImageByPath(p.getImagePath());
        if (img != null && !img.isError()) {
            ImageView iv = new ImageView(img);
            iv.setFitWidth(62);
            iv.setFitHeight(62);
            iv.setPreserveRatio(true);
            imageBox.getChildren().setAll(iv);
        } else {
            Label fallback = new Label("x");
            fallback.setStyle("-fx-font-size: 28px;");
            imageBox.getChildren().setAll(fallback);
        }

        Label nameLabel = new Label(p.getProductName());
        nameLabel.setStyle("-fx-font-size: 14.5px; -fx-font-weight: 800; -fx-text-fill: #1B2230;");

        Label brandLabel = new Label(p.getBrand());
        brandLabel.getStyleClass().add("badge-teal");

        Label unitPriceLabel = new Label("₪ " + String.format("%.2f", p.getPrice()) + " / unit");
        unitPriceLabel.getStyleClass().add("product-brand");

        VBox infoBox = new VBox(6, nameLabel, brandLabel, unitPriceLabel);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setPrefWidth(230);

        Button minusBtn = new Button("-");
        Button plusBtn = new Button("+");
        Label qtyValueLabel = new Label(String.valueOf(qty));
        qtyValueLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 800; -fx-text-fill: #1B2230;");
        qtyValueLabel.setMinWidth(30);
        qtyValueLabel.setAlignment(Pos.CENTER);

        minusBtn.getStyleClass().add("stepper-btn");
        plusBtn.getStyleClass().add("stepper-btn");

        HBox stepper = new HBox(10, minusBtn, qtyValueLabel, plusBtn);
        stepper.setAlignment(Pos.CENTER);
        stepper.setPadding(new Insets(4));

        Label subtotalRowLabel = new Label("₪ " + String.format("%.2f", p.getPrice() * qty));
        subtotalRowLabel.getStyleClass().add("price-text");
        subtotalRowLabel.setMinWidth(110);
        subtotalRowLabel.setAlignment(Pos.CENTER_RIGHT);

        Button removeBtn = new Button("Remove");
        removeBtn.getStyleClass().add("btn-danger-soft");
        removeBtn.setStyle(removeBtn.getStyle() + "-fx-min-width: 90px; -fx-padding: 8 10 8 10; -fx-font-size: 11.5px; -fx-font-weight: 700;");

        minusBtn.setOnAction(e -> {
            int newQty = qty - 1;
            if (newQty <= 0) {
                CartManager.removeFromCart(p);
            } else {
                CartManager.updateQuantity(p, newQty);
            }
            refreshCartItems();
        });
        plusBtn.setOnAction(e -> {
            int availableStock = productDAO.getStockQuantity(p.getProductId());
            if (qty + 1 > availableStock) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText(null);
                alert.setContentText("Only " + availableStock + " unit(s) of \"" +
                    p.getProductName() + "\" are in stock.");
                alert.showAndWait();
                return;
            }
            CartManager.updateQuantity(p, qty + 1);
            refreshCartItems();
        });
        removeBtn.setOnAction(e -> {
            CartManager.removeFromCart(p);
            refreshCartItems();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(18, imageBox, infoBox, spacer, stepper, subtotalRowLabel, removeBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(16));
        row.getStyleClass().add("card-white");

        return row;
    }

    // SUMMARY PANEL
    private VBox buildSummaryPanel() {

        Label title = new Label("Order Summary");
        title.getStyleClass().add("section-title");
        title.setStyle("-fx-font-size: 17px;");

        subtotalLabel = new Label("₪ 0.00");
        shippingLabel = new Label("₪ 30.00");
        totalLabel = new Label("₪ 0.00");

        HBox subtotalRow = summaryRow("Subtotal", subtotalLabel, false);
        HBox shippingRow = summaryRow("Shipping", shippingLabel, false);

        Region divider = thinDivider();

        HBox totalRow = summaryRow("Total", totalLabel, true);

        Button checkoutBtn = new Button("Proceed to Checkout");
        checkoutBtn.setMaxWidth(Double.MAX_VALUE);
        checkoutBtn.getStyleClass().add("btn-primary");
        checkoutBtn.setOnAction(e -> {
            if (!CartManager.getCart().isEmpty()) {
                Main.setView(new PaymentView());
            }
        });

        Button continueBtn = new Button("Continue Shopping");
        continueBtn.setMaxWidth(Double.MAX_VALUE);
        continueBtn.getStyleClass().add("btn-link");
        continueBtn.setStyle("-fx-font-size: 12px; -fx-padding: 8 0 0 0;");
        continueBtn.setOnAction(e -> Main.setView(new ProductsView(0)));

        VBox panel = new VBox(16, title, subtotalRow, shippingRow, divider, totalRow,
            checkoutBtn, continueBtn);
        panel.getStyleClass().add("card-white-shadow");
        panel.setPadding(new Insets(26));
        panel.setPrefWidth(330);

        return panel;
    }

    private Region thinDivider() {
        Region div = new Region();
        div.setPrefHeight(1);
        div.getStyleClass().add("thin-divider");
        return div;
    }

    private HBox summaryRow(String label, Label valueLabel, boolean isTotal) {
        Label lbl = new Label(label);
        lbl.setStyle(
            isTotal
                ? "-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #1B2230;"
                : "-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #6B7280;");
        if (isTotal) {
            valueLabel.getStyleClass().add("price-text-large");
        } else {
            valueLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #1B2230;");
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox row = new HBox(lbl, spacer, valueLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void updateSummary() {
        double subtotal = CartManager.getTotal();
        double shipping = CartManager.getCart().isEmpty() ? 0 : 30.0;
        double total = subtotal + shipping;

        subtotalLabel.setText("₪ " + String.format("%.2f", subtotal));
        shippingLabel.setText("₪ " + String.format("%.2f", shipping));
        totalLabel.setText("₪ " + String.format("%.2f", total));
    }
}
