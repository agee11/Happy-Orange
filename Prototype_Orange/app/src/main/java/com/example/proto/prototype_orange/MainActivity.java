package com.example.proto.prototype_orange;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

    //drawer variables
    private DrawerLayout myDrawer;
    private ListView myListView;
    private String[] myNavigationItems;
    private ActionBarDrawerToggle myDrawerToggle;

    //Fragment manager variables
    FragmentManager fragmentManager;

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

        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_frame, new HomeFragment());
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

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

        //Enable ActionBar app icon to behave as toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().show();

    }

    //Class to listen to item clicks in drawer
    //Close drawer when item is clicked
    private class DrawerItemClickListener implements ListView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            myDrawer.closeDrawer(myListView);
            selectItem(position);
        }
    }

    //What to do when drawer item is clicked
    //Replace current content frame fragment with the one selected
    private void selectItem(int position){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        switch(position){
            case 0: fragmentTransaction.replace(R.id.content_frame, new HomeFragment());
                    break;
            case 2: fragmentManager.popBackStack("dishInfo", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentTransaction.replace(R.id.content_frame, new FilterMenu());
                    break;
        }
        fragmentTransaction.commit();

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

