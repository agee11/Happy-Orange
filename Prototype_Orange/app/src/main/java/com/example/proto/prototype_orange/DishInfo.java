package com.example.proto.prototype_orange;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DishInfo extends AppCompatActivity {

    Dish dish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_info);
        Intent intent = getIntent();
        dish = (Dish) intent.getSerializableExtra("dish");
        TextView name = (TextView) findViewById(R.id.infoName);
        TextView price = (TextView) findViewById(R.id.infoPrice);
        TextView restaurant = (TextView) findViewById(R.id.infoRestaurant);
        TextView cuisine = (TextView) findViewById(R.id.infoCuisine);
        TextView spicy = (TextView) findViewById(R.id.infoSpice);
        TextView vegetarian = (TextView) findViewById(R.id.infoVegetarian);
        ImageView image = (ImageView) findViewById(R.id.infoImage);

        name.setText(dish.getName());
        price.setText("$" + dish.getPrice());
        restaurant.setText(dish.getRestaurant());
        cuisine.setText(dish.getCuisine());
        spicy.setText("Spicy: " + dish.isSpicy());
        vegetarian.setText("Vegetarian: " + dish.isVegetarian());
        image.setImageResource(dish.getImageId());


    }
}
