package com.example.proto.prototype_orange;

import android.view.View;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * Created by orang on 5/13/2016.
 */
public class Dish implements Serializable{
    private double price;
    private String name;
    private boolean spicy;
    private boolean vegetarian;
    private String restaurant;
    private String cuisine;
    private int imageId;

    public Dish(){

    }
    double getPrice(){return price;}
    void setPrice(double n){
        price = n;
    }

    String getName(){return name;}
    void setName(String n){
        name = n;
    }

    boolean isSpicy(){return spicy;}
    void setSpice(boolean n){
        spicy = n;
    }

    boolean isVegetarian(){return vegetarian;}
    void setVegetarian(boolean n){
        vegetarian = n;
    }

    String getRestaurant(){return restaurant;}
    void setRestaurant(String n){
        restaurant = n;
    }

    String getCuisine(){return cuisine;}
    void setCuisine(String n){
        cuisine = n;
    }

    int getImageId(){return imageId;}
    void setImageId(int n){
        imageId = n;
    }

}
