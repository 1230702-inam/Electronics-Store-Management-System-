package models;

public class Product {

    private int productId;
    private String productName;
    private String brand;
    private String model;
    private double price;
    private String imagePath;
    private int quantity;
    private int categoryId;

    public Product() {
    }

    public Product(int productId, String productName, String brand,String model, double price, String imagePath,
    		                  int quantity, int categoryId) {
                   
        this.productId = productId;
        this.productName = productName;
        this.brand = brand;
        this.model = model;
        this.price = price;
        this.imagePath = imagePath;
        this.quantity = quantity;
        this.categoryId = categoryId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
