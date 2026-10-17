package views;

import application.Main;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class OrderConfirmationView extends BorderPane {

    private int orderId;
    private double total;

    public OrderConfirmationView(int orderId, double total) {
        this.orderId = orderId;
        this.total = total;
        getStyleClass().add("root");
        VBox main = new VBox(0,
            buildNavBar(),
            buildContent()
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

        HBox navbar = new HBox(logo);
        navbar.getStyleClass().add("navbar");
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setPadding(new Insets(14, 36, 14, 36));
        navbar.setMinHeight(64);
        return navbar;
    }

    private ScrollPane buildContent() {

        Label badge = new Label("ORDER CONFIRMED");
        badge.setStyle(
            "-fx-font-size: 11px; -fx-font-weight: 800; -fx-text-fill: #1E7F4B;" +
            "-fx-background-color: rgba(30,127,75,0.10); -fx-background-radius: 6px;" +
            "-fx-padding: 5 12 5 12;");

        Label title = new Label("Order placed successfully");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: 800; -fx-text-fill: #1B2230;");

        Label sub = new Label("Thank you for your purchase. A confirmation will be sent to your email shortly.");
        sub.getStyleClass().add("section-sub");
        sub.setWrapText(true);
        sub.setMaxWidth(420);

        Label orderIdLabel = new Label("Order #" + orderId);
        orderIdLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: #1E7F7B;");

        Label totalLabel = new Label("Total Paid: ₪ " + String.format("%.2f", total));
        totalLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #1B2230;");

        VBox detailsBox = new VBox(8, orderIdLabel, totalLabel);
        detailsBox.setAlignment(Pos.CENTER);
        detailsBox.setPadding(new Insets(20));
        detailsBox.getStyleClass().add("card-white");

        Button continueBtn = new Button("Continue Shopping");
        continueBtn.getStyleClass().add("btn-primary");
        continueBtn.setOnAction(e -> Main.setView(new DashboardView()));

        Button viewOrdersBtn = new Button("View My Orders");
        viewOrdersBtn.getStyleClass().add("btn-secondary");
        viewOrdersBtn.setOnAction(e -> Main.setView(new MyOrdersView()));

        HBox btnRow = new HBox(12, continueBtn, viewOrdersBtn);
        btnRow.setAlignment(Pos.CENTER);

        VBox box = new VBox(16, badge, title, sub, detailsBox, btnRow);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(70, 40, 70, 40));

        ScrollPane scroll = new ScrollPane(box);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.getStyleClass().add("transparent-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return scroll;
    }
}
