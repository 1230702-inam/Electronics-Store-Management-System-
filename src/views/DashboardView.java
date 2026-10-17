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

public class DashboardView extends BorderPane {

    private ProductDAO productDAO = new ProductDAO();

    public DashboardView() {
        getStyleClass().add("root");
        VBox main = new VBox(0,
            buildTopStrip(),
            buildNavBar(),
            buildScrollContent()
        );
        VBox.setVgrow(main, Priority.ALWAYS);
        setCenter(main);
    }

    // TOP ANNOUNCEMENT STRIP
    private HBox buildTopStrip() {
        Label msg = new Label("Free shipping on orders over ₪500   •   0566-765-211   •   Ramallah, Beitunia, Al-Bireh");
        msg.getStyleClass().add("top-strip-text");

        HBox strip = new HBox(msg);
        strip.getStyleClass().add("top-strip");
        strip.setAlignment(Pos.CENTER);
        strip.setPadding(new Insets(6, 0, 6, 0));
        return strip;
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

        Button homeBtn   = navBtn("Home",       e -> Main.setView(new DashboardView()));
        Button ordersBtn = navBtn("My Orders",  e -> Main.setView(new MyOrdersView()));

        HBox navLinks = new HBox(6, homeBtn, ordersBtn);
        navLinks.setAlignment(Pos.CENTER);

        Button reportsBtn = new Button("Reports");
        reportsBtn.getStyleClass().add("reports-btn");
        reportsBtn.setOnAction(e -> Main.setView(new ReportsView()));

        Button cartBtn = new Button("Cart");
        cartBtn.getStyleClass().add("cart-icon-btn");
        cartBtn.setOnAction(e -> Main.setView(new OrderView()));

        HBox rightIcons = new HBox(12, reportsBtn, cartBtn);
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
            buildHeroSection(),
            buildCategoriesSection(),
            buildFeaturedSection()
        );

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("transparent-scroll");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return scroll;
    }

    // HERO SECTION
    private StackPane buildHeroSection() {

        StackPane hero = new StackPane();
        hero.getStyleClass().add("hero-section");
        hero.setMinHeight(360);

        Label title = new Label("Elevate Your\nDigital Life");
        title.getStyleClass().add("hero-title");

        Label subtitle = new Label("Welcome to ZainTech's curated collection.\nDiscover premium electronics for modern lifestyles.");
        subtitle.getStyleClass().add("hero-subtitle");

        MenuButton categoriesBtn = new MenuButton("Browse Categories");
        categoriesBtn.getStyleClass().add("hero-categories-btn");

        // قائمة الكاتيجوريز هلأ بتجي من قاعدة البيانات مباشرة
        ArrayList<String[]> categories = productDAO.getAllCategories();
        for (String[] cat : categories) {
            int catId = Integer.parseInt(cat[0]);
            String catName = cat[1];
            MenuItem item = new MenuItem(catName);
            item.setOnAction(e -> Main.setView(new ProductsView(catId)));
            categoriesBtn.getItems().add(item);
        }

        Button shopBtn = new Button("Shop Now");
        shopBtn.getStyleClass().add("btn-outline");
        shopBtn.setOnAction(e -> Main.setView(new ProductsView(0)));

        HBox btnRow = new HBox(14, categoriesBtn, shopBtn);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        VBox leftContent = new VBox(14, title, subtitle, btnRow);
        leftContent.setAlignment(Pos.CENTER_LEFT);
        leftContent.setMaxWidth(520);

        StackPane rightContent = new StackPane();
        rightContent.setMinWidth(340);
        rightContent.setAlignment(Pos.CENTER);

        // صورة الهيرو ثابتة بالكود من جافا
        Image heroImg = CategoryImages.loadImageByPath("hero.png");
        if (heroImg != null && !heroImg.isError()) {
            ImageView heroIv = new ImageView(heroImg);
            heroIv.setFitWidth(320);
            heroIv.setPreserveRatio(true);
            rightContent.getChildren().add(heroIv);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox heroContent = new HBox(40, leftContent, spacer, rightContent);
        heroContent.setAlignment(Pos.CENTER);
        heroContent.setPadding(new Insets(50, 80, 50, 80));

        hero.getChildren().add(heroContent);
        return hero;
    }

    // CATEGORIES SECTION
    // كل الكاتيجوريز هلأ بتنجاب من قاعدة البيانات 
    //(جدول CATEGORY)
    // بما فيها  تبع كل كاتيجوري (Image_Path)
    private VBox buildCategoriesSection() {

        Label sectionTitle = new Label("Shop by Category");
        sectionTitle.getStyleClass().add("section-title");
        sectionTitle.setStyle("-fx-font-size: 20px;");

        Label sectionSub = new Label("Browse our range of premium electronics");
        sectionSub.getStyleClass().add("section-sub");

        VBox headerBox = new VBox(4, sectionTitle, sectionSub);

        FlowPane catsGrid = new FlowPane();
        catsGrid.setHgap(18);
        catsGrid.setVgap(18);
        catsGrid.setPrefWrapLength(1000);

        ArrayList<String[]> categories = productDAO.getAllCategories();
        for (String[] cat : categories) {
            // cat[0]=ID, cat[1]=Name, cat[2]=Description, cat[3]=Image_Path
            int catId = Integer.parseInt(cat[0]);
            String catName = cat[1];
            String imagePath = cat[3];
            VBox card = buildCategoryCard(catId, catName, imagePath);
            catsGrid.getChildren().add(card);
        }

        VBox section = new VBox(20, headerBox, catsGrid);
        section.setPadding(new Insets(36, 36, 32, 36));
        section.getStyleClass().add("root");

        return section;
    }

    private VBox buildCategoryCard(int catId, String name, String imagePath) {

        // كل البطاقات بنفس اللون 
        String accentBg = "#E3EEEC";

        StackPane iconBox = new StackPane();
        iconBox.setPrefSize(96, 96);
        iconBox.setStyle("-fx-background-color: " + accentBg + "; -fx-background-radius: 8px;");

        Image img = CategoryImages.loadImageByPath(imagePath);
        if (img != null && !img.isError()) {
            ImageView iv = new ImageView(img);
            iv.setFitWidth(72);
            iv.setFitHeight(72);
            iv.setPreserveRatio(true);
            iconBox.getChildren().add(iv);
        } else {
            Label fallback = new Label("x");
            fallback.setStyle("-fx-font-size: 40px;");
            iconBox.getChildren().add(fallback);
        }

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("category-name");

        VBox card = new VBox(14, iconBox, nameLabel);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(232, 170);
        card.setPadding(new Insets(20));
        card.getStyleClass().add("category-card");
        card.setCursor(Cursor.HAND);

        card.setOnMouseClicked(e -> Main.setView(new ProductsView(catId)));

        return card;
    }

    // FEATURED PRODUCTS
    private VBox buildFeaturedSection() {

        Label title = new Label("Featured Products");
        title.getStyleClass().add("section-title");
        title.setStyle("-fx-font-size: 20px;");

        Label sub = new Label("Hand-picked items just for you");
        sub.getStyleClass().add("section-sub");

        VBox titleBox = new VBox(4, title, sub);

        Button seeAll = new Button("See All Products");
        seeAll.getStyleClass().add("btn-link");
        seeAll.setOnAction(e -> Main.setView(new ProductsView(0)));

        HBox hdr = new HBox();
        Region hdrSpacer = new Region();
        hdr.getChildren().addAll(titleBox, hdrSpacer, seeAll);
        HBox.setHgrow(hdrSpacer, Priority.ALWAYS);
        hdr.setAlignment(Pos.CENTER_LEFT);

        FlowPane grid = new FlowPane();
        grid.setHgap(18);
        grid.setVgap(18);
        grid.setPadding(new Insets(6, 0, 0, 0));

        ArrayList<Product> products = productDAO.getAllProducts();
        int count = 0;
        for (Product p : products) {
            if (count >= 8) break;
            grid.getChildren().add(buildProductCard(p));
            count++;
        }

        VBox section = new VBox(20, hdr, grid);
        section.setPadding(new Insets(36, 36, 44, 36));
        section.getStyleClass().add("root");

        return section;
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

        Label nameLabel = new Label(p.getProductName());
        nameLabel.getStyleClass().add("product-name");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(206);
        nameLabel.setMinHeight(34);

        Label brandLabel = new Label(p.getBrand());
        brandLabel.getStyleClass().add("product-brand");

        Label priceLabel = new Label("₪ " + String.format("%.2f", p.getPrice()));
        priceLabel.getStyleClass().add("price-text");

        VBox card = new VBox(8, imageBox, brandLabel, nameLabel, priceLabel);
        card.setPrefWidth(232);
        card.setPadding(new Insets(14));
        card.getStyleClass().add("card-white");
        card.setCursor(Cursor.HAND);

        card.setOnMouseClicked(e -> showProductPopup(p));

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

        VBox infoBox = new VBox(6, brandLabel, nameLabel, priceLabel, stockLabel, btnRow);
        infoBox.setAlignment(Pos.TOP_LEFT);

        HBox content = new HBox(22, imgBox, infoBox);
        content.setAlignment(Pos.CENTER_LEFT);

        popup.getChildren().addAll(topBar, content);
        overlay.getChildren().add(popup);

        Main.showOverlay(overlay);
    }
}
