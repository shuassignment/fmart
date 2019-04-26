package com.my.fresh.mart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.my.fresh.mart.Fragments.FragmentAdmin;
import com.my.fresh.mart.Fragments.FragmentCustomers;

public class ParidizActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final int FIREBASE_UI_SIGN_IN = 9001;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paridiz);
/*

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdView mAdView1 = (AdView) findViewById(R.id.adView1);
        AdView mAdView2 = (AdView) findViewById(R.id.adView2);
        AdView mAdView3 = (AdView) findViewById(R.id.adView3);
        AdView mAdView4 = (AdView) findViewById(R.id.adView4);
        AdView mAdView5 = (AdView) findViewById(R.id.adView5);
        AdView mAdView6 = (AdView) findViewById(R.id.adView6);
        AdView mAdView7 = (AdView) findViewById(R.id.adView7);
        AdView mAdView8 = (AdView) findViewById(R.id.adView8);

        AdRequest adRequest = new AdRequest.Builder()
                .build();
        AdRequest adRequest2 = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        mAdView1.loadAd(adRequest);
        mAdView2.loadAd(adRequest2);
        mAdView3.loadAd(adRequest);
        mAdView4.loadAd(adRequest);
        mAdView5.loadAd(adRequest);
        mAdView6.loadAd(adRequest);
        mAdView7.loadAd(adRequest);
        mAdView8.loadAd(adRequest);
*/


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (count == 0) {
            android.support.v7.app.AlertDialog.Builder buider = new android.support.v7.app.AlertDialog.Builder(ParidizActivity.this);
            buider.setMessage("Are you sure you want to exit the application ?")
                    .setCancelable(false)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            finish();
                        }
                    });
            android.support.v7.app.AlertDialog alert = buider.create();
            alert.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement

//        if (item.getItemId() == R.id.action_search) {
//            Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment f = null;

        if (id == R.id.nav_product) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user == null) {
                startActivityForResult(Config.FIREBASE_LOGIN(), FIREBASE_UI_SIGN_IN);
            } else {
                Intent intent = new Intent(ParidizActivity.this, CategoriesActivity.class);
                intent.putExtra("upload", true);
                startActivity(intent);
            }

        } else if (id == R.id.nav_acount) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user == null) {
                startActivityForResult(Config.FIREBASE_LOGIN(), FIREBASE_UI_SIGN_IN);
            } else {
                // Name, email address, and profile photo Url

                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                Config.PARIDIZ.child("Customers").child(user.getUid()).child("device_token").setValue(deviceToken);

                String name = user.getDisplayName();
                String email = user.getEmail();

                AlertDialog.Builder buider = new AlertDialog.Builder(ParidizActivity.this);
                buider.setTitle("Account Information")
                        .setMessage("Name :" + name + "\nEmail :" + email)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//                                Toast.makeText(ParidizActivity.this, "Account information feature is coming soon...", Toast.LENGTH_SHORT).show();
                            }
                        });
                AlertDialog alert = buider.create();
                alert.show();


                Uri photoUrl = user.getPhotoUrl();
                String phoneNumber = user.getPhoneNumber();

                // Check if user's email is verified
                boolean emailVerified = user.isEmailVerified();

                // The user's ID, unique to the Firebase project. Do NOT use this value to
                // authenticate with your backend server, if you have one. Use
                // FirebaseUser.getToken() instead.
                String uid = user.getUid();
            }

//            Intent intent = new Intent(ParidizActivity.this, LoginActivity.class);
//            startActivity(intent);

        } else if (id == R.id.nav_customers) {
            //todo show customer list

            f = new FragmentCustomers();
            Toast.makeText(this, "This feature is coming soon...", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_orders) {

            //            f=new FragmentCategories();

            Intent intent = new Intent(ParidizActivity.this, OrdersActivity.class);
            intent.putExtra("owner", true);
            startActivity(intent);

        } else if (id == R.id.nav_see_product) {
            // Go to product Activity

            Intent intent = new Intent(this, CategoriesActivity.class);
            intent.putExtra("upload", false);
            intent.putExtra("admin", true);

            startActivity(intent);

//            Intent intent = new Intent(ParidizActivity.this, ProductsPatanjaliStoreActivity.class);
//            startActivity(intent);
        } else if (id == R.id.nav_cust_ver) {
            //Customer Version
            Intent intent = new Intent(ParidizActivity.this, CustomerMainActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_terms_condition) {
            //Customer Version
            f = new FragmentAdmin();
        }


        if (f != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right
            ).addToBackStack(Config.MAIN_STACK).replace(R.id.containerdemo, f).commit();
        }

//        nav_cust_ver
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void ownerLogout(MenuItem item) {

        Config.FIREBASE_LOGOUT(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }
}
