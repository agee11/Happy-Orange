package com.example.proto.prototype_orange;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Random;

/**
 * Created by Andrew on 8/6/2016.
 */
public class HomeFragment extends Fragment {
    private Button pass, save, go;
    private TextView dishName;
    private ImageView dishImage;

    int current;
    int[] position;
    float xPos;
    float xOffset;
    private GestureDetector gestureDetector;

    //Fragment manager variables
    FragmentManager fragmentManager;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Egg Roll");
    StorageReference storageRef;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_layout, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        pass = (Button) getView().findViewById(R.id.Pass);
        save = (Button) getView().findViewById(R.id.Save);
        go = (Button) getView().findViewById(R.id.Go);
        dishName = (TextView) getView().findViewById(R.id.dishName);
        dishImage = (ImageView) getView().findViewById(R.id.dishImage);

        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://happy-orange.appspot.com/");
        fragmentManager = getFragmentManager();

        gestureDetector = new GestureDetector(new GestureListener());
        position = new int [2];
        xOffset = -1;

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
                    dishImage.setX(xPos);
                    xOffset = -1;
                }
                gestureDetector.onTouchEvent(event);

                return true;
            }
        });

        go.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                //startActivity(intent);
            }
        });
    }

    //Get next dish when user swipes left or taps on pass button
    void passDish(){
        myRef = database.getReference("Big Mac");
        Random rn = new Random();
        int prev;

        //System.out.println(current);
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
                storageRef.child("eggroll.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){

                    @Override
                    public void onSuccess(Uri uri){
                        //System.out.println("Storage URI: " + uri);
                        DownloadImageTask downloadImageTask = new DownloadImageTask(dishImage);
                        downloadImageTask.execute(uri);
                    }
                });
                break;
        }
    }

    //Listens for tap on dish image
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if(dishName.getText() == "Egg Roll" || dishName.getText() == "Big Mac") {
                //eggroll.setName("" + dishName.getText());

                //Intent intent = new Intent(MainActivity.this, DishInfo.class);
                //intent.putExtra("dish", eggroll);
                //startActivity(intent);
                Bundle bundle = new Bundle();
                bundle.putString("dish", "" + dishName.getText());
                DishInfo dishInfo = new DishInfo();
                dishInfo.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.drawer_layout, dishInfo);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
            return true;
        }

    }
}
