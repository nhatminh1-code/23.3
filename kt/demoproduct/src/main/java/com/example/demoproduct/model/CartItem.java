package com.example.demoproduct.model;

import java.io.Serializable;

public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long productId;
    private String productName;
    private String productImage;
    private Double price;
    private Integer quantity;

    public CartItem() {
    }

    public CartItem(Long productId, String productName, String productImage,
                    Double price, Integer quantity) {
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.price = price;
        this.quantity = quantity;
    }

    public Double getSubtotal() {
        return price * quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}