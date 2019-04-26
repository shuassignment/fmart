package com.my.fresh.mart.Model;

/**
 * Created by intel on 09-Dec-17.
 */

public class Orders {
//    private String address,delivery,description,image,mobileNo,name,payment,price,quantity,title,total,uid;

    private String ProductKey;
    private String Image;
    private String ProductName;
    private String OfferPrice;
    private String MRP;
    private String Weight;
    private String Quantity;
    private String Category;
    private String AddedInCartAt;
    private String PlacedOrderAt;
    private String Uid;
    private String Payment;
    private String OrderId;

    public Orders(){

    }

    public Orders(String productKey, String image, String productName, String offerPrice, String mrp, String weight, String quantity, String category, String addedInCartAt, String placedOrderAt, String uid, String payment, String orderId)
    {
        ProductKey = productKey;
        Image = image;
        ProductName = productName;
        OfferPrice = offerPrice;
        MRP = mrp;
        Weight = weight;
        Quantity = quantity;
        Category = category;
        AddedInCartAt = addedInCartAt;
        PlacedOrderAt = placedOrderAt;
        Uid = uid;
        Payment = payment;
        OrderId = orderId;
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

    public String getPlacedOrderAt() {
        return PlacedOrderAt;
    }

    public void setPlacedOrderAt(String placedOrderAt) {
        PlacedOrderAt = placedOrderAt;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getPayment() {
        return Payment;
    }

    public void setPayment(String payment) {
        Payment = payment;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }
}
