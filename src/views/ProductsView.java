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

import java.util.ArrayList;

public class ProductsView extends BorderPane {

    private ProductDAO productDAO = new ProductDAO();
    private FlowPane productsGrid;
    private TextField searchField;
    private int currentCategoryId; 
    private ArrayList<Product> currentProductList = new ArrayList<>();
    private Label resultsCountLabel;
    private ComboBox<String> sortBox;

    private ArrayList<String[]> categoriesCache;

    public ProductsView(int categoryId) {
        this.currentCategoryId = categoryId;
        build();
    }

    public ProductsView(String keyword) {
        this.currentCategoryId = -1; // search mode
        build();
        searchField.setText(keyword);
        runSearch(keyword);
    }

    private void build() {
        getStyleClass().add("root");
        categoriesCache = productDAO.getAllCategories();

        VBox main = new VBox(0,
            buildNavBar(),
            buildScrollContent()
        );
        VBox.setVgrow(main, Priority.ALWAYS);
        setCenter(main);
    }

    private String categoryName(int categoryId) {
        if (categoryId == 0) return "All Products";
        for (String[] cat : categoriesCache) {
            if (Integer.parseInt(cat[0]) == categoryId) return cat[1];
        }
        return "Products";
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

        Button homeBtn   = navBtn("Home",      e -> Main.setView(new DashboardView()));
        Button ordersBtn = navBtn("My Orders", e -> Main.setView(new MyOrdersView()));

        HBox navLinks = new HBox(6, homeBtn, ordersBtn);
        navLinks.setAlignment(Pos.CENTER);

        searchField = new TextField();
        searchField.setPromptText("Search products...");
        searchField.setPrefWidth(200);
        searchField.getStyleClass().add("search-field");
        searchField.setOnAction(e -> runSearch(searchField.getText().trim()));

        Button reportsBtn = new Button("Reports");
        reportsBtn.getStyleClass().add("reports-btn");
        reportsBtn.setOnAction(e -> Main.setView(new ReportsView()));

        Button cartBtn = new Button("Cart");
        cartBtn.getStyleClass().add("cart-icon-btn");
        cartBtn.setStyle(cartBtn.getStyle() + "-fx-background-radius: 18px; -fx-min-width: 60px;");
        cartBtn.setOnAction(e -> Main.setView(new OrderView()));

        HBox rightIcons = new HBox(12, searchField, reportsBtn, cartBtn);
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

        VBox content = new VBox(0,
            buildPageHeader(),
            buildFilterChips(),
            buildProductsSection()
        );

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("transparent-scroll");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return scroll;
    }

    // PAGE HEADER
    private StackPane buildPageHeader() {
        StackPane header = new StackPane();
        header.getStyleClass().add("page-header");
        header.setMinHeight(110);

        String title = currentCategoryId == -1? "Search Results"
        		 : categoryName(currentCategoryId);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("page-header-title");

        Label breadcrumb = new Label("Home  /  " + title);
        breadcrumb.getStyleClass().add("page-breadcrumb");

        VBox box = new VBox(8, breadcrumb, titleLabel);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(0, 0, 0, 40));

        StackPane.setAlignment(box, Pos.CENTER_LEFT);
        header.getChildren().add(box);

        return header;
    }

    // الكاتيجوريز هون هلأ بتجي من قاعدة البيانات مش من قائمة ثابتة
    private HBox buildFilterChips() {

        HBox chipsRow = new HBox(10);
        chipsRow.setPadding(new Insets(20, 36, 16, 36));
        chipsRow.setAlignment(Pos.CENTER_LEFT);

        chipsRow.getChildren().add(buildChip(0, "All"));
        for (String[] cat : categoriesCache) {
            int catId = Integer.parseInt(cat[0]);
            String catName = cat[1];
            chipsRow.getChildren().add(buildChip(catId, catName));
        }

        ScrollPane scroll = new ScrollPane(chipsRow);
        scroll.setFitToHeight(true);
        scroll.getStyleClass().add("transparent-scroll");
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setPrefHeight(60);

        HBox wrapper = new HBox(scroll);
        wrapper.getStyleClass().add("root");
        HBox.setHgrow(scroll, Priority.ALWAYS);
        return wrapper;
    }

    private Button buildChip(int catId, String name) {
        boolean active = (catId == currentCategoryId) || (catId == 0 && currentCategoryId == 0);
        Button chip = new Button(name);
        chip.getStyleClass().add(active ? "chip-active" : "chip");
        chip.setOnAction(e -> Main.setView(new ProductsView(catId)));
        return chip;
    }

    // PRODUCTS GRID SECTION
    private VBox buildProductsSection() {

        productsGrid = new FlowPane();
        productsGrid.setHgap(18);
        productsGrid.setVgap(18);

        resultsCountLabel = new Label();
        resultsCountLabel.getStyleClass().add("results-count");

        sortBox = new ComboBox<>();
        sortBox.getItems().addAll(
            "Sort: Featured", "Price: Low to High", "Price: High to Low", "Name: A-Z");
        sortBox.setValue("Sort: Featured");
        sortBox.getStyleClass().add("sort-combo");
        sortBox.setOnAction(e -> applySortAndReload());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbar = new HBox(12, resultsCountLabel, spacer, sortBox);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(0, 0, 16, 0));

        loadProducts();

        VBox section = new VBox(toolbar, productsGrid);
        section.setPadding(new Insets(8, 36, 44, 36));
        section.getStyleClass().add("root");

        return section;
    }

    private void applySortAndReload() {
        String sort = sortBox.getValue();
        if (sort == null) return;
        switch (sort) {
            case "Price: Low to High":
                currentProductList.sort((a, b) -> Double.compare(a.getPrice(), b.getPrice()));
                break;
            case "Price: High to Low":
                currentProductList.sort((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
                break;
            case "Name: A-Z":
                currentProductList.sort((a, b) -> a.getProductName().compareToIgnoreCase(b.getProductName()));
                break;
            default:
                break;
        }
        renderGrid(currentProductList);
    }

    private void renderGrid(ArrayList<Product> products) {
        productsGrid.getChildren().clear();
        resultsCountLabel.setText("Showing " + products.size() + " product" + (products.size() == 1 ? "" : "s"));

        if (products.isEmpty()) {
            Label empty = new Label("No products found.");
            empty.getStyleClass().add("empty-state-label");
            productsGrid.getChildren().add(empty);
            return;
        }

        for (Product p : products) {
            productsGrid.getChildren().add(buildProductCard(p));
        }
    }

    private void loadProducts() {
        ArrayList<Product> products;
        if (currentCategoryId == 0 || currentCategoryId == -1) {
            products = productDAO.getAllProducts();
        } else {
            products = productDAO.getProductsByCategory(currentCategoryId);
        }
        currentProductList = products;
        renderGrid(currentProductList);
    }

    private void runSearch(String keyword) {
        ArrayList<Product> results = keyword.isEmpty()
            ? productDAO.getAllProducts()
            : productDAO.searchProducts(keyword);
        currentProductList = results;
        if (results.isEmpty()) {
            productsGrid.getChildren().clear();
            resultsCountLabel.setText("Showing 0 products");
            Label empty = new Label("No products match \"" + keyword + "\".");
            empty.getStyleClass().add("empty-state-label");
            productsGrid.getChildren().add(empty);
            return;
        }
        renderGrid(currentProductList);
    }

    private VBox buildProductCard(Product p) {

        StackPane imageBox = new StackPane();
        imageBox.setPrefSize(206, 170);
        imageBox.getStyleClass().add("image-box");

        Image img = CategoryImages.loadImageByPath(p.getImagePath());
        if (img != null && !img.isError()) {
            ImageView iv = new ImageView(img);
            iv.setFitWidth(130);
            iv.setFitHeight(130);
            iv.setPreserveRatio(true);
            imageBox.getChildren().setAll(iv);
        } else {
            Label fallback = new Label("x");
            fallback.setStyle("-fx-font-size: 44px;");
            imageBox.getChildren().setAll(fallback);
        }

        Label brandLabel = new Label(p.getBrand());
        brandLabel.getStyleClass().add("product-brand");

        Label nameLabel = new Label(p.getProductName());
        nameLabel.getStyleClass().add("product-name");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(206);
        nameLabel.setMinHeight(34);

        Label priceLabel = new Label("₪ " + String.format("%.2f", p.getPrice()));
        priceLabel.getStyleClass().add("price-text");

        Button viewBtn = new Button("View Details");
        viewBtn.getStyleClass().add("view-details-btn");
        viewBtn.setMaxWidth(Double.MAX_VALUE);
        viewBtn.setOnAction(e -> showProductPopup(p));

        Button addBtn = new Button("+");
        addBtn.getStyleClass().add("quick-add-btn");

        Label quickAddFeedback = new Label();
        quickAddFeedback.getStyleClass().add("success-note");
        quickAddFeedback.setStyle("-fx-font-size: 10px;");
        addBtn.setOnAction(e -> {
            CartManager.addToCart(p, 1);
            quickAddFeedback.setText("Added to cart");
        });

        HBox actionRow = new HBox(8, viewBtn, addBtn);
        actionRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(viewBtn, Priority.ALWAYS);

        VBox card = new VBox(8, imageBox, brandLabel, nameLabel, priceLabel, actionRow, quickAddFeedback);
        card.setPrefWidth(232);
        card.setPadding(new Insets(14));
        card.getStyleClass().add("product-grid-card");
        card.setCursor(Cursor.HAND);

        return card;
    }

    // PRODUCT POPUP
    private void showProductPopup(Product p) {

        StackPane overlay = new StackPane();
        overlay.getStyleClass().add("modal-overlay");
        overlay.setAlignment(Pos.CENTER);
        overlay.setOnMouseClicked(e -> {
            if (e.getTarget() == overlay) Main.closeOverlay();
        });

        VBox popup = new VBox(18);
        popup.getStyleClass().add("modal-card");
        popup.setPadding(new Insets(26));
        popup.setMaxWidth(520);
        popup.setMaxHeight(420);

        Button closeBtn = new Button("Close");
        closeBtn.getStyleClass().add("modal-close-btn");
        closeBtn.setStyle("-fx-background-radius: 8px; -fx-min-width: 56px; -fx-font-size: 11px; -fx-padding: 6 10 6 10;");
        closeBtn.setOnAction(e -> Main.closeOverlay());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topBar = new HBox(spacer, closeBtn);

        StackPane imgBox = new StackPane();
        imgBox.setPrefSize(180, 160);
        imgBox.getStyleClass().add("image-box");

        Image img = CategoryImages.loadImageByPath(p.getImagePath());
        if (img != null && !img.isError()) {
            ImageView iv = new ImageView(img);
            iv.setFitWidth(120);
            iv.setFitHeight(120);
            iv.setPreserveRatio(true);
            imgBox.getChildren().setAll(iv);
        } else {
            Label fallback = new Label("x");
            fallback.setStyle("-fx-font-size: 44px;");
            imgBox.getChildren().setAll(fallback);
        }

        Label brandLabel = new Label(p.getBrand());
        brandLabel.getStyleClass().add("product-brand");

        Label nameLabel = new Label(p.getProductName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #1B2230;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(270);

        Label modelLabel = new Label("Model: " + p.getModel());
        modelLabel.getStyleClass().add("section-sub");
        modelLabel.setWrapText(true);
        modelLabel.setMaxWidth(270);

        Label priceLabel = new Label("₪ " + String.format("%.2f", p.getPrice()));
        priceLabel.getStyleClass().add("price-text-large");

        int availableStock = p.getQuantity();
        Label stockLabel = new Label(
            availableStock > 0 ? availableStock + " in stock" : "Out of stock");
        stockLabel.getStyleClass().add(availableStock > 0 ? "product-brand" : "field-error-label");

        int spinnerMax = Math.max(availableStock, 1);
        Spinner<Integer> qtySpinner = new Spinner<>(1, spinnerMax, 1);
        qtySpinner.setStyle("-fx-pref-width: 80px;");
        qtySpinner.setDisable(availableStock <= 0);

        Button addBtn = new Button("Add to Cart");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setStyle("-fx-background-radius: 8px; -fx-padding: 9 20 9 20;");
        addBtn.setDisable(availableStock <= 0);

        Label successLabel = new Label();
        successLabel.getStyleClass().add("success-note");

        addBtn.setOnAction(e -> {
            int qty = qtySpinner.getValue();
            CartManager.addToCart(p, qty);
            successLabel.setText("Added to cart");
        });

        HBox btnRow = new HBox(12, qtySpinner, addBtn, successLabel);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        VBox infoBox = new VBox(6, brandLabel, nameLabel, modelLabel, priceLabel, stockLabel, btnRow);
        infoBox.setAlignment(Pos.TOP_LEFT);

        HBox content = new HBox(22, imgBox, infoBox);
        content.setAlignment(Pos.CENTER_LEFT);

        popup.getChildren().addAll(topBar, content);
        overlay.getChildren().add(popup);

        Main.showOverlay(overlay);
    }
}
