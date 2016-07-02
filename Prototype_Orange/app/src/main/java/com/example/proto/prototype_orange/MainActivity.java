package com.example.proto.prototype_orange;

import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;
import android.view.GestureDetector;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity{


    Button pass, save, go;
    TextView dishName;
    ImageView dishImage;
    int current;
    int[] position;
    float xPos;
    int yPos;
    Dish eggroll;
    private GestureDetector gestureDetector;
    float xOffset;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Eggroll");

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pass = (Button) findViewById(R.id.Pass);
        save = (Button) findViewById(R.id.Save);
        go = (Button) findViewById(R.id.Go);
        dishName = (TextView) findViewById(R.id.dishName);
        dishImage = (ImageView) findViewById(R.id.dishImage);
        position = new int[2];
        gestureDetector = new GestureDetector(new GestureListener());
        xOffset = -1;
        eggroll = new Dish();
        eggroll.setCuisine("Chinese");
        eggroll.setPrice(4.99);
        eggroll.setName("Egg Roll");
        eggroll.setSpice(false);
        eggroll.setVegetarian(false);
        eggroll.setRestaurant("Panda Express");
        eggroll.setImageId(R.drawable.eggroll);

///////////////////////////////////////////////////////////////////
        //myRef.setValue("Whats up");
        //System.out.println(dataSnapshot.getValue(String.class));
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
////////////////////////////////////////////////////////////////////////

        pass.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
            passDish();
            //System.out.println(rn);
            }
        });
 /*       dishImage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(dishName.getText() == "Egg Roll") {
                    Intent intent = new Intent(MainActivity.this, DishInfo.class);
                    intent.putExtra("dish", eggroll);
                    startActivity(intent);
                }
            }
        });
        */
        dishImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dishImage.getLocationOnScreen(position);
                if(xOffset == -1) {
                    xPos = position[0];
                    xOffset = event.getRawX() - dishImage.getX();
                }
                dishImage.setX(event.getRawX() - xOffset);

                if(event.getAction() == MotionEvent.ACTION_UP){
                    if((dishImage.getWidth()/2 + dishImage.getX()) <= 0){
                        passDish();
                        System.out.println("Pass on Swipe");
                    }
                    //System.out.println(dishImage.getWidth()/2 + " " + dishImage.getX());
                    dishImage.setX(xPos);
                    xOffset = -1;
                }
                gestureDetector.onTouchEvent(event);

                return true;
            }
        });

        //dishImage.setOnTouchListener(this);


        go.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //if(dishName.getText() == "Egg Roll") {
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(intent);
                //}
            }
        });

    }

    void passDish(){
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
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if(dishName.getText() == "Egg Roll") {
                Intent intent = new Intent(MainActivity.this, DishInfo.class);
                intent.putExtra("dish", eggroll);
                startActivity(intent);
            }

            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1.getX() > e2.getX()){
                Toast.makeText(getApplication().getBaseContext(), "Fling Left", Toast.LENGTH_SHORT).show();
            }else if(e1.getX() < e2.getX()){
                Toast.makeText(getApplication().getBaseContext(), "Fling Right", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplication().getBaseContext(), "Fling meh", Toast.LENGTH_SHORT).show();
            }
            return true;
        }


    }
}

