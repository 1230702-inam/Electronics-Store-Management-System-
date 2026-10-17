package views;

import application.Main;
import dao.CustomerDAO;
import dao.OrderDAO;
import dao.PaymentDAO;
import dao.ProductDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import models.Customer;
import models.Product;
import utils.CartManager;

import java.util.LinkedHashMap;
import java.util.Map;

public class PaymentView extends BorderPane {

    private CustomerDAO customerDAO = new CustomerDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();
    private ProductDAO productDAO = new ProductDAO();

    private TextField nameField, emailField, phoneField, addressField;

    // Credit card fields (shown conditionally)
    private VBox cardFormBox;
    private TextField cardNumberField, cardNameField, cardExpiryField, cardCvvField;

    private ToggleGroup paymentGroup;
    private Label errorLabel;
    private Label subtotalLabel, shippingLabel, totalLabel;

    public PaymentView() {
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

        Label stepLabel = new Label("Step 2 of 3 - Checkout");
        stepLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #1E7F7B;");

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        HBox navbar = new HBox(16, logo, spacer1, stepLabel);
        navbar.getStyleClass().add("navbar");
        navbar.setAlignment(Pos.CENTER);
        navbar.setPadding(new Insets(14, 36, 14, 36));
        navbar.setMinHeight(64);

        return navbar;
    }

    // SCROLL CONTENT
    private ScrollPane buildScrollContent() {

        StackPane header = new StackPane();
        header.getStyleClass().add("page-header");
        header.setMinHeight(100);

        Label title = new Label("Checkout");
        title.getStyleClass().add("page-header-title");
        Label sub = new Label("Enter your details to complete the order");
        sub.getStyleClass().add("page-header-sub");
        VBox headerBox = new VBox(8, title, sub);
        headerBox.setPadding(new Insets(0, 0, 0, 40));
        StackPane.setAlignment(headerBox, Pos.CENTER_LEFT);
        header.getChildren().add(headerBox);

        HBox bodyRow = buildCheckoutBody();
        VBox content = new VBox(0, header, bodyRow);
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("transparent-scroll");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return scroll;
    }

    // CHECKOUT BODY
    private HBox buildCheckoutBody() {

        VBox leftCol = new VBox(20,
            buildCustomerInfoCard(),
            buildPaymentMethodCard()
        );
        leftCol.setPrefWidth(640);

        VBox rightPanel = buildOrderSummaryCard();
        HBox row = new HBox(28, leftCol, rightPanel);
        row.setPadding(new Insets(32, 40, 44, 40));
        row.setAlignment(Pos.TOP_LEFT);

        return row;
    }

    // CUSTOMER INFO CARD 
    private VBox buildCustomerInfoCard() {

        Label title = new Label("Customer Information");
        title.getStyleClass().add("section-title");
        title.setStyle("-fx-font-size: 16px;");

        nameField = styledField("Full Name");
        emailField = styledField("Email Address");
        phoneField = styledField("Phone Number (e.g. 0591234567)");
        addressField = styledField("Delivery Address");

        VBox nameBox = fieldGroup("Full Name", nameField);
        VBox emailBox = fieldGroup("Email Address", emailField);
        VBox phoneBox = fieldGroup("Phone Number", phoneField);
        VBox addressBox = fieldGroup("Delivery Address", addressField);

        HBox row1 = new HBox(16, nameBox, emailBox);
        HBox row2 = new HBox(16, phoneBox, addressBox);
        HBox.setHgrow(nameBox, Priority.ALWAYS);
        HBox.setHgrow(emailBox, Priority.ALWAYS);
        HBox.setHgrow(phoneBox, Priority.ALWAYS);
        HBox.setHgrow(addressBox, Priority.ALWAYS);

        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-banner");
        errorLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-weight: 600;");
        errorLabel.setWrapText(true);

        VBox card = new VBox(16, title, row1, row2, errorLabel);
        card.getStyleClass().add("card-white");
        card.setPadding(new Insets(24));

        return card;
    }

    private VBox fieldGroup(String label, TextField field) {
        Label lbl = new Label(label.toUpperCase());
        lbl.getStyleClass().add("form-label");
        VBox box = new VBox(6, lbl, field);
        return box;
    }

    private TextField styledField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefHeight(40);
        field.getStyleClass().add("form-field");
        return field;
    }

    // FIELD VALIDATION 
    private boolean isNameValid() {
        String value = nameField.getText().trim();
        return value.length() >= 3 && value.matches("[A-Za-z\\u0600-\\u06FF ]+");
    }

    private boolean isEmailValid() {
        String value = emailField.getText().trim();
        return value.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    }

    private boolean isPhoneValid() {
        String value = phoneField.getText().trim();
        return value.matches("^0[5-9][0-9]{8}$");
    }

    private boolean isAddressValid() {
        String value = addressField.getText().trim();
        return value.length() >= 6;
    }

    private boolean isCardNumberValid() {
        String digits = cardNumberField.getText().replaceAll("[^0-9]", "");
        return digits.length() == 16;
    }

    private boolean isExpiryValid() {
        String value = cardExpiryField.getText().trim();
        return value.matches("^(0[1-9]|1[0-2])/[0-9]{2}$");
    }

    private boolean isCvvValid() {
        String value = cardCvvField.getText().trim();
        return value.length() >= 3;
    }

    // PAYMENT METHOD CARD
    private VBox buildPaymentMethodCard() {

        Label title = new Label("Payment Method");
        title.getStyleClass().add("section-title");
        title.setStyle("-fx-font-size: 16px;");

        paymentGroup = new ToggleGroup();

        RadioButton cardRadio = new RadioButton();
        RadioButton cashRadio = new RadioButton();
        RadioButton bankRadio = new RadioButton();

        VBox cardOption = paymentOption(cardRadio, "Credit / Debit Card", "Visa, MasterCard accepted", "Credit Card", true);
        VBox cashOption = paymentOption(cashRadio, "Cash on Delivery", "Pay when your order arrives", "Cash", false);
        VBox bankOption = paymentOption(bankRadio, "Bank Transfer", "Direct transfer to our account", "Debit Card", false);

        VBox options = new VBox(12, cardOption, cashOption, bankOption);

        cardFormBox = buildCardForm();
        cardFormBox.managedProperty().bind(cardFormBox.visibleProperty());
        cardFormBox.setVisible(true);

        cardRadio.selectedProperty().addListener((o, was, isNow) -> {
            if (isNow) cardFormBox.setVisible(true);
        });
        cashRadio.selectedProperty().addListener((o, was, isNow) -> {
            if (isNow) cardFormBox.setVisible(false);
        });
        bankRadio.selectedProperty().addListener((o, was, isNow) -> {
            if (isNow) cardFormBox.setVisible(false);
        });

        VBox card = new VBox(16, title, options, cardFormBox);
        card.getStyleClass().add("card-white");
        card.setPadding(new Insets(24));

        return card;
    }

    private VBox paymentOption(RadioButton radio, String name, String desc, String value, boolean selected) {

        radio.setToggleGroup(paymentGroup);
        radio.setSelected(selected);
        radio.setUserData(value);

        Label nameLbl = new Label(name);
        nameLbl.setStyle("-fx-font-size: 13.5px; -fx-font-weight: 700; -fx-text-fill: #1B2230;");

        Label descLbl = new Label(desc);
        descLbl.getStyleClass().add("product-brand");

        VBox textBox = new VBox(2, nameLbl, descLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(14, textBox, spacer, radio);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14, 16, 14, 16));

        VBox wrapper = new VBox(row);
        wrapper.getStyleClass().add(selected ? "payment-option-selected" : "payment-option");
        wrapper.setCursor(Cursor.HAND);

        wrapper.setOnMouseClicked(e -> radio.setSelected(true));
        radio.selectedProperty().addListener((obs, was, isNow) -> {
            wrapper.getStyleClass().removeAll("payment-option", "payment-option-selected");
            wrapper.getStyleClass().add(isNow ? "payment-option-selected" : "payment-option");
        });

        return wrapper;
    }

    // CREDIT CARD DETAILS FORM
    private VBox buildCardForm() {

        Label formTitle = new Label("Card Details");
        formTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: 800; -fx-text-fill: #1B2230;");

        cardNameField = styledField("Name on card");
        cardNumberField = styledField("1234 5678 9012 3456");
        cardExpiryField = styledField("MM/YY");
        cardCvvField = styledField("CVV");

        cardNumberField.textProperty().addListener((obs, oldV, newV) -> {
            String digits = newV.replaceAll("[^0-9]", "");
            if (digits.length() > 16) digits = digits.substring(0, 16);
            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < digits.length(); i++) {
                if (i > 0 && i % 4 == 0) formatted.append(" ");
                formatted.append(digits.charAt(i));
            }
            if (!formatted.toString().equals(newV)) {
                cardNumberField.setText(formatted.toString());
                cardNumberField.positionCaret(formatted.length());
            }
        });

        cardExpiryField.textProperty().addListener((obs, oldV, newV) -> {
            String digits = newV.replaceAll("[^0-9]", "");
            if (digits.length() > 4) digits = digits.substring(0, 4);
            String formatted = digits;
            if (digits.length() >= 3) {
                formatted = digits.substring(0, 2) + "/" + digits.substring(2);
            }
            if (!formatted.equals(newV)) {
                cardExpiryField.setText(formatted);
                cardExpiryField.positionCaret(formatted.length());
            }
        });

        cardCvvField.textProperty().addListener((obs, oldV, newV) -> {
            String digits = newV.replaceAll("[^0-9]", "");
            if (digits.length() > 4) digits = digits.substring(0, 4);
            if (!digits.equals(newV)) {
                cardCvvField.setText(digits);
                cardCvvField.positionCaret(digits.length());
            }
        });

        VBox cardNumberBox = fieldGroup("Card Number", cardNumberField);
        VBox cardNameBox = fieldGroup("Name on Card", cardNameField);
        VBox expiryBox = fieldGroup("Expiry Date", cardExpiryField);
        VBox cvvBox = fieldGroup("CVV", cardCvvField);

        HBox expiryCvvRow = new HBox(16, expiryBox, cvvBox);
        HBox.setHgrow(expiryBox, Priority.ALWAYS);
        HBox.setHgrow(cvvBox, Priority.ALWAYS);

        VBox formBox = new VBox(14, formTitle, cardNumberBox, cardNameBox, expiryCvvRow);
        formBox.setPadding(new Insets(16, 0, 0, 0));

        return formBox;
    }

    // ORDER SUMMARY CARD
    private VBox buildOrderSummaryCard() {

        Label title = new Label("Order Summary");
        title.getStyleClass().add("section-title");
        title.setStyle("-fx-font-size: 17px;");

        VBox itemsList = new VBox(10);
        LinkedHashMap<Product, Integer> cart = CartManager.getCart();
        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            itemsList.getChildren().add(summaryItemRow(entry.getKey(), entry.getValue()));
        }

        ScrollPane itemsScroll = new ScrollPane(itemsList);
        itemsScroll.setFitToWidth(true);
        itemsScroll.setPrefHeight(Math.min(220, cart.size() * 60 + 10));
        itemsScroll.getStyleClass().add("transparent-scroll");

        Region divider1 = thinDivider();

        subtotalLabel = new Label("₪ " + String.format("%.2f", CartManager.getTotal()));
        shippingLabel = new Label("₪ 30.00");
        totalLabel = new Label("₪ " + String.format("%.2f", CartManager.getTotal() + 30));

        HBox subtotalRow = summaryRow("Subtotal", subtotalLabel, false);
        HBox shippingRow = summaryRow("Shipping", shippingLabel, false);

        Region divider2 = thinDivider();

        HBox totalRow = summaryRow("Total", totalLabel, true);

        Button placeOrderBtn = new Button("Place Order");
        placeOrderBtn.setMaxWidth(Double.MAX_VALUE);
        placeOrderBtn.getStyleClass().add("btn-primary");
        placeOrderBtn.setOnAction(e -> handlePlaceOrder());

        Button backBtn = new Button("Back to Cart");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.getStyleClass().add("btn-link");
        backBtn.setStyle("-fx-font-size: 12px; -fx-padding: 8 0 0 0;");
        backBtn.setOnAction(e -> Main.setView(new OrderView()));

        VBox panel = new VBox(14, title, itemsScroll, divider1, subtotalRow, shippingRow,
            divider2, totalRow, placeOrderBtn, backBtn);
        panel.getStyleClass().add("card-white-shadow");
        panel.setPadding(new Insets(26));
        panel.setPrefWidth(330);

        return panel;
    }

    private HBox summaryItemRow(Product p, int qty) {
        Label nameLbl = new Label(p.getProductName() + "  x" + qty);
        nameLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #1B2230;");
        nameLbl.setWrapText(true);
        nameLbl.setMaxWidth(190);

        Label priceLbl = new Label("₪ " + String.format("%.2f", p.getPrice() * qty));
        priceLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #6B7280;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox row = new HBox(nameLbl, spacer, priceLbl);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
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

    // PLACE ORDER 
    private void handlePlaceOrder() {

        errorLabel.setText("");

        boolean nameOk    = isNameValid();
        boolean emailOk   = isEmailValid();
        boolean phoneOk   = isPhoneValid();
        boolean addressOk = isAddressValid();

        String paymentMethod = "Credit Card";
        if (paymentGroup.getSelectedToggle() != null) {
            paymentMethod = (String) paymentGroup.getSelectedToggle().getUserData();
        }

        boolean cardOk = true;
        if (paymentMethod.equals("Credit Card")) {
            cardOk = isCardNumberValid() && isExpiryValid() && isCvvValid();
        }

        if (!nameOk || !emailOk || !phoneOk || !addressOk) {
            errorLabel.setText("Please fill in your name, a valid email, phone number, and address.");
            return;
        }

        if (!cardOk) {
            errorLabel.setText("Please complete the card details correctly.");
            return;
        }

        if (CartManager.getCart().isEmpty()) {
            errorLabel.setText("Your cart is empty.");
            return;
        }

      
        LinkedHashMap<Product, Integer> cartCheck = CartManager.getCart();
        for (Map.Entry<Product, Integer> entry : cartCheck.entrySet()) {
            Product p = entry.getKey();
            int requestedQty = entry.getValue();
            int availableQty = productDAO.getStockQuantity(p.getProductId());
            if (availableQty < requestedQty) {
                errorLabel.setText("Not enough stock for \"" + p.getProductName() +
                    "\". Only " + availableQty + " left.");
                return;
            }
        }

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        try {
            Customer existing = customerDAO.getCustomerByEmail(email);
            int customerId;
            if (existing != null) {
                customerId = existing.getCustomerId();
            } else {
                Customer newCustomer = new Customer();
                newCustomer.setCustomerName(name);
                newCustomer.setEmail(email);
                newCustomer.setPhone(phone);
                newCustomer.setAddress(address);
                customerId = customerDAO.addCustomer(newCustomer);
            }

            if (customerId <= 0) {
                errorLabel.setText("Could not save customer info. Please try again.");
                return;
            }

            double subtotal = CartManager.getTotal();
            double shipping = 30.0;
            double total = subtotal + shipping;

            int orderId = orderDAO.createOrder(customerId, total);
            if (orderId <= 0) {
                errorLabel.setText("Could not create order. Please try again.");
                return;
            }

            LinkedHashMap<Product, Integer> cart = CartManager.getCart();
            for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
                Product p = entry.getKey();
                int qty = entry.getValue();
                orderDAO.addOrderItem(orderId, p.getProductId(), qty, p.getPrice());

                
                boolean decreased = productDAO.decreaseStock(p.getProductId(), qty);
                if (!decreased) {
                    errorLabel.setText("Stock for \"" + p.getProductName() +
                        "\" changed and is no longer sufficient. Please review your cart.");
                    return;
                }
            }

            paymentDAO.addPayment(orderId, paymentMethod, total);

            CartManager.clearCart();
            Main.setView(new OrderConfirmationView(orderId, total));

        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Something went wrong. Please try again.");
        }
    }
}
