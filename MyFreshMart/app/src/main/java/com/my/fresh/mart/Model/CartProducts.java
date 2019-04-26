package com.my.fresh.mart.Model;

/**
 * Created by intel on 07-Nov-17.
 */

public class CartProducts {

    private String ProductKey;
    private String Image;
    private String ProductName;
    private String OfferPrice;
    private String MRP;
    private String Weight;
    private String Quantity;
    private String Category;
    private String AddedInCartAt;


    public CartProducts() {

    }

    public CartProducts(String productKey, String image, String productName, String offerPrice, String mrp, String weight, String quantity, String category, String addedInCartAt) {
        ProductKey = productKey;
        Image = image;

        ProductName = productName;
        OfferPrice = offerPrice;
        MRP = mrp;
        Weight = weight;
        Quantity = quantity;
        Category = category;
        AddedInCartAt = addedInCartAt;
    }


    public String getProductKey() {
        return ProductKey;
    }

    public void setProductKey(String productKey) {
        ProductKey = productKey;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getOfferPrice() {
        return OfferPrice;
    }

    public void setOfferPrice(String offerPrice) {
        OfferPrice = offerPrice;
    }

    public String getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP = MRP;
    }

    public String getWeight() {
        return Weight;
    }

    public void setWeight(String weight) {
        Weight = weight;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getAddedInCartAt() {
        return AddedInCartAt;
    }

    public void setAddedInCartAt(String addedInCartAt) {
        AddedInCartAt = addedInCartAt;
    }
}
