package com.example.travelmantics;

import java.io.Serializable;
//Model class for the travel deal object
public class TravelDeal implements Serializable {
    private String id;
    private String title;
    private String description;
    private String price;
    private String imageUrl;

//a non instantiable class
    public TravelDeal(){}

    public TravelDeal( String title, String description, String price, String imageUrl) {
        this.setId(id);
        this.setTitle(title);
        this.setDescription(description);
        this.setPrice(price);
        this.setImageUrl(imageUrl);
    }



    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
