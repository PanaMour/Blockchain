package com.eap.plh24;

public class Product {
    private int registrationNumber;
    private int productId;
    private String productTitle;
    private long timestamp;
    private double price;
    private String description;
    private String category;
    private int previousRegistrationNumber;

    public Product(int productId, String productTitle, double price, String description, String category) {
        this.productId = productId;
        this.productTitle = productTitle;
        this.price = price;
        this.description = description;
        this.category = category;
        this.timestamp = System.currentTimeMillis(); // Current timestamp when the product is created
        this.registrationNumber = -1; // Will be set later when added to the blockchain
        this.previousRegistrationNumber = -1; // Default value for products without a previous registration
    }

    // Getters and setters for all fields

    public int getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(int registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public int getPreviousRegistrationNumber() {
        return previousRegistrationNumber;
    }

    public void setPreviousRegistrationNumber(int previousRegistrationNumber) {
        this.previousRegistrationNumber = previousRegistrationNumber;
    }

    // Other methods as needed

    @Override
    public String toString() {
        return "Product{" +
                "registrationNumber=" + registrationNumber +
                ", productId=" + productId +
                ", productTitle='" + productTitle + '\'' +
                ", timestamp=" + timestamp +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", previousRegistrationNumber=" + previousRegistrationNumber +
                '}';
    }
}
