package com.example.proto.prototype_orange;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.proto.prototype_orange.Dish;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    Button pass, save, go;
    TextView dishName;
    ImageView dishImage;
    int current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pass = (Button) findViewById(R.id.Pass);
        save = (Button) findViewById(R.id.Save);
        go = (Button) findViewById(R.id.Go);
        dishName = (TextView) findViewById(R.id.dishName);
        dishImage = (ImageView) findViewById(R.id.dishImage);

        final Dish eggroll = new Dish();
        eggroll.setCuisine("Chinese");
        eggroll.setPrice(4.99);
        eggroll.setName("Egg Roll");
        eggroll.setSpice(false);
        eggroll.setVegetarian(false);
        eggroll.setRestaurant("Panda Express");
        eggroll.setImageId(R.drawable.eggroll);

        pass.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Random rn = new Random();
                int prev;
                System.out.println(current);
                do{
                    prev = current;
                    current = rn.nextInt(4);
                }while(current == prev);

                switch(current) {
                    case 0: dishName.setText("Cheese Cake");
                            dishImage.setImageResource(R.drawable.cheesecake);
                            break;
                    case 1: dishName.setText("Big Mac");
                            dishImage.setImageResource(R.drawable.big_mac);
                            break;
                    case 2: dishName.setText("Curly Fries");
                            dishImage.setImageResource(R.drawable.curly_fries);
                            break;
                    case 3: dishName.setText("Egg Roll");
                            dishImage.setImageResource(R.drawable.eggroll);
                            break;
                }
                System.out.println(rn);
            }
        });
        dishImage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(dishName.getText() == "Egg Roll") {
                    Intent intent = new Intent(MainActivity.this, DishInfo.class);
                    intent.putExtra("dish", eggroll);
                    startActivity(intent);
                }
            }
        });

        go.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(dishName.getText() == "Egg Roll") {
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

}
