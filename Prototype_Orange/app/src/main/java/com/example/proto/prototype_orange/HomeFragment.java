package com.example.proto.prototype_orange;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * Created by Andrew on 8/6/2016.
 */
public class HomeFragment extends Fragment {
    private Button pass, save, go;
    private TextView dishName;
    private ImageView dishImage;
    private boolean filterConflict;

    int current;
    int[] position;
    float xPos;
    float xOffset;
    private GestureDetector gestureDetector;
    Bundle savedState;

    //Fragment manager variables
    FragmentManager fragmentManager;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
                getDish();
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
                        getDish();
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
    private void getDish(){
        myRef = database.getReference();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Random rn = new Random();
                Stack<Integer> prev = new Stack<Integer>();
                String[] filterItems;
                SharedPreferences preferences = getActivity().getSharedPreferences("com.example.proto.prototype_orange", Context.MODE_PRIVATE);
                boolean dietConflict, mealConflict;

                do {
                    prev.push(current);
                    PREV:
                    for (int i:prev) {
                        if(current == i){
                            current = rn.nextInt(23);
                            break PREV;
                        }
                    }

                    //Check if food fits user filter settings
                    filterItems = getResources().getStringArray(R.array.filter_options);
                    filterConflict = true;
                    for (String s: filterItems) {
                        //System.out.println("FILTER " + s + ": " + preferences.getBoolean(s, false));
                        //System.out.println("FILTER " + s + ": " + (boolean) dataSnapshot.child("" + current).child(s).getValue());
                        if((preferences.getBoolean(s, false) == true) && ((boolean)dataSnapshot.child("" + current).child(s).getValue() == true)) {
                            filterConflict = false;
                            break;
                        }
                    }

                    //Check if food fits user diet restrictions
                    filterItems = getResources().getStringArray(R.array.diet_options);
                    dietConflict = false;
                    for (String s: filterItems) {
                        //System.out.println("FILTER " + s + ": " + preferences.getBoolean(s, false));
                        //System.out.println("FILTER " + s + ": " + (boolean) dataSnapshot.child("" + current).child(s).getValue());
                        if((preferences.getBoolean(s, false) == true) && ((boolean)dataSnapshot.child("" + current).child(s).getValue() == false)) {
                            dietConflict = true;
                            break;
                        }

                    }
                    //System.out.println("Diet " + dietConflict);
                    System.out.println("Filter " + filterConflict);
                    System.out.println("Diet " + dietConflict);
                }while(dietConflict || filterConflict);

                dishName.setText("" + dataSnapshot.child("" + current).child("Name").getValue());
                storageRef.child("" + dataSnapshot.child("" + current).child("Image").getValue()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
                    @Override
                    public void onSuccess(Uri uri){
                        DownloadImageTask downloadImageTask = new DownloadImageTask(dishImage);
                        downloadImageTask.execute(uri);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkFilter(DatabaseReference ref){

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String[] filterItems = getResources().getStringArray(R.array.filter_options);
                SharedPreferences preferences = getActivity().getSharedPreferences("com.example.proto.prototype_orange", Context.MODE_PRIVATE);

                filterConflict = false;
                for (String s: filterItems) {
                    System.out.println("FILTER " + s + ": " + preferences.getBoolean(s, false));
                    System.out.println("FILTER " + s + ": " + (boolean) dataSnapshot.child(s).getValue());
                    if((preferences.getBoolean(s, false) == true) && ((boolean)dataSnapshot.child(s).getValue() == false)) {
                        filterConflict = true;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Listens for tap on dish image
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Bundle bundle = new Bundle();
            bundle.putString("dish", "" + current);
            DishInfo dishInfo = new DishInfo();
            dishInfo.setArguments(bundle);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.content_frame, dishInfo);
            fragmentTransaction.addToBackStack("dishInfo");
            fragmentTransaction.commit();
            return true;
        }

    }
}
