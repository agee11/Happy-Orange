package com.example.proto.prototype_orange;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Random;
import android.view.GestureDetector;
import android.widget.Toast;

import com.google.android.gms.phenotype.Configuration;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity{


    private Button pass, save, go;
    private TextView dishName;
    private ImageView dishImage;
    int current;
    int[] position;
    float xPos;
    int yPos;
    Dish eggroll;
    private GestureDetector gestureDetector;
    float xOffset;

    //Server Variables
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Egg Roll");
    StorageReference storageRef;

    //drawer variables
    private DrawerLayout myDrawer;
    private ListView myListView;
    private String[] myNavigationItems;
    private ActionBarDrawerToggle myDrawerToggle;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = myDrawer.isDrawerOpen(myListView);
        //Insert code to hide certain items from UI when drawer is open.

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pass = (Button) findViewById(R.id.Pass);
        save = (Button) findViewById(R.id.Save);
        go = (Button) findViewById(R.id.Go);
        dishName = (TextView) findViewById(R.id.dishName);
        dishImage = (ImageView) findViewById(R.id.dishImage);

        //drawer variables tied to layout
        myDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        myListView = (ListView) findViewById(R.id.left_drawer);
        myNavigationItems = getResources().getStringArray(R.array.navigation_array);

        //Fill drawer with navigation items
        myListView.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, myNavigationItems));
        myListView.setOnItemClickListener(new DrawerItemClickListener());

        myDrawerToggle = new ActionBarDrawerToggle(this, myDrawer, R.string.drawer_open, R.string.drawer_close){
             public void onDrawerClosed(View view){
                 super.onDrawerClosed(view);
                 getSupportActionBar().setTitle("Happy Orange");
                 invalidateOptionsMenu();
             }
            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Menu");
                invalidateOptionsMenu();
            }
        };

        //Set the drawer toggle as the listener
        myDrawer.setDrawerListener(myDrawerToggle);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().show();
        position = new int[2];
        gestureDetector = new GestureDetector(new GestureListener());
        xOffset = -1;
        eggroll = new Dish();

        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://happy-orange.appspot.com/");


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
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
            }
        });

    }

    //Class to listen to item clicks in drawer
    private class DrawerItemClickListener implements ListView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    //What to do when item is clicked
    private void selectItem(int position){
        System.out.println(myNavigationItems[position]);
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
                eggroll.setName("" + dishName.getText());
                Intent intent = new Intent(MainActivity.this, DishInfo.class);
                intent.putExtra("dish", eggroll);
                startActivity(intent);
            }
            return true;
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstancesState){
        super.onPostCreate(savedInstancesState);
        myDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(myDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

