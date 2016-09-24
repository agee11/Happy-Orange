package com.example.proto.prototype_orange;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DishInfo extends Fragment {

    String dish;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    StorageReference storageRef;


    public DishInfo(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_dish_info);
        //Intent intent = getIntent();
        //dish = (Dish) getActivity().getIntent().getSerializableExtra("dish");

        dish = getArguments().getString("dish");
        //Set Reference to point to correct location in database
        myRef = database.getReference(dish);
        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://happy-orange.appspot.com/");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("OnCreateView");
        return inflater.inflate(R.layout.activity_dish_info, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        //Attach variables to views on screen
        final TextView name = (TextView) getView().findViewById(R.id.infoName);
        final TextView price = (TextView) getView().findViewById(R.id.infoPrice);
        final TextView restaurant = (TextView) getView().findViewById(R.id.infoRestaurant);
        final TextView cuisine = (TextView) getView().findViewById(R.id.infoCuisine);
        final TextView spicy = (TextView) getView().findViewById(R.id.infoSpice);
        final TextView vegetarian = (TextView) getView().findViewById(R.id.infoVegetarian);
        final ImageView image = (ImageView) getView().findViewById(R.id.infoImage);

        //Set variables to views on screen
        name.setText(dish);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.child("Cuisine").getValue());
                price.setText("$" + dataSnapshot.child("Price").getValue());
                restaurant.setText("" + dataSnapshot.child("Restaurant").getValue());
                cuisine.setText("" + dataSnapshot.child("Cuisine").getValue());
                spicy.setText("Spicy: " + dataSnapshot.child("Spicy").getValue());
                vegetarian.setText("Vegetarian: " + dataSnapshot.child("Vegetarian").getValue());
                storageRef.child("" + dataSnapshot.child("Image").getValue()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){

                    @Override
                    public void onSuccess(Uri uri){
                        //System.out.println("Storage URI: " + uri);
                        DownloadImageTask downloadImageTask = new DownloadImageTask(image);
                        downloadImageTask.execute(uri);
                    }
                });
                //image.setImageResource(dish.getImageId());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}
