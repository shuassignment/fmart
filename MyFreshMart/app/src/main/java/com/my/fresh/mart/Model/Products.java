package com.my.fresh.mart.Model;

/**
 * Created by intel on 07-Nov-17.
 */

public class Products {


    private String Image;
    private String ProductName;
    private String Weight;
    private String Price;
    private boolean Availability;


    private String Remark;
    private String Category;

    public Products()
    {

    }

    public Products(String image, String productName, String weight, boolean availability, String price, String availableQuantity, String remark)
    {
        this.Image = image;
        ProductName = productName;
        Weight = weight;
        Price = price;
        Availability = availability;
//        AvailableQuantity = availableQuantity;
        Remark = remark;
    }


    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        this.Image = image;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getWeight() {
        return Weight;
    }

    public void setWeight(String weight) {
        Weight = weight;
    }


    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getPrice() {
        return Price;
    }


    public void setPrice(String price) {
        Price = price;
    }

    public boolean isAvailability() {
        return Availability;
    }

    public void setAvailability(boolean availability) {
        Availability = availability;
    }


}
