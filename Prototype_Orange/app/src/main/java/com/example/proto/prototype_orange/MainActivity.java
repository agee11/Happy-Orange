package com.example.proto.prototype_orange;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;
import android.view.GestureDetector;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    StorageReference storageRef;

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

        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://happy-orange.appspot.com/");

///////////////////////////////////////////////////////////////////
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
                //dishImage.setImageResource(R.drawable.eggroll);
                //Bitmap bmp = BitmapFactory.decodeStream();
                //dishImage.setImageBitmap(bmp);
                storageRef.child("eggroll.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){

                    @Override
                    public void onSuccess(Uri uri){
                        System.out.println("Storage URI: " + uri);
                        //URL url = new URL(uri);
                        DownloadImageTask downloadImageTask = new DownloadImageTask();
                        downloadImageTask.execute(uri);
                        //uriToImage(uri);
                    }
                });
                break;
        }
    }

    private class DownloadImageTask extends AsyncTask<Uri, Void, Void>{
        Bitmap bmp;

        @Override
        protected Void doInBackground(Uri... params) {
            try {
                //InputStream imageStream = getContentResolver().openInputStream(uri);
                URL url = new URL(params[0].toString());
                System.out.println("Storage URL: " + url);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dishImage.setImageBitmap(bmp);
            //dishImage.set
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

