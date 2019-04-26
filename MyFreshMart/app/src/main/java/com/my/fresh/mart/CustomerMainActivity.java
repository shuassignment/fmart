package com.my.fresh.mart;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.my.fresh.mart.Fragments.FragMainScreen;
import com.my.fresh.mart.Fragments.FragmentAccountInfo;
import com.my.fresh.mart.Fragments.FragmentCategories;
import com.my.fresh.mart.Fragments.FragmentProductList;

import java.util.ArrayList;
import java.util.List;

import static com.my.fresh.mart.ParidizActivity.FIREBASE_UI_SIGN_IN;

public class CustomerMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    MaterialSearchView searchView;
    TextView tvName, tvNumber;

    //    Firebase

    FirebaseUser user;

    DatabaseHelper databaseHelper;
    int width;
    TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_paridiz_main);

        final SharedPreferences.Editor editor = getSharedPreferences(Config.SHARED_PREFERENCE_TNC, MODE_PRIVATE).edit();
        Config.TNC.child("MinCartValueToCheckout").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    String minCartValue=dataSnapshot.getValue().toString();
                    editor.putString("MinCartValue",minCartValue);
                    editor.apply();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        final SharedPreferences.Editor editor = getSharedPreferences(Config.SHARED_PREFERENCE_TNC, MODE_PRIVATE).edit();
        Config.TNC.child("DeliveryCharges").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    String minCartValue= String.valueOf(dataSnapshot.getValue());
                    Log.d("minCartValue",minCartValue);
                    editor.putString("DeliveryCharges",minCartValue);
                    editor.apply();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searching();
        retriveFreomFirebaseAndSaveInSQLite();


        Fragment f = new FragMainScreen();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right
        ).addToBackStack(Config.MAIN_STACK).replace(R.id.containerCustomerActivity, f).commit();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tvName = navigationView.getHeaderView(0).findViewById(R.id.custName);
        tvName.setVisibility(View.GONE);
        tvNumber = navigationView.getHeaderView(0).findViewById(R.id.custNumber);
        tvNumber.setVisibility(View.GONE);

        loadNavigationHeader();

    }

    @Override
    public void onBackPressed() {

        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }
        int count = getSupportFragmentManager().getBackStackEntryCount();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (count == 1) {
            AlertDialog.Builder buider = new AlertDialog.Builder(CustomerMainActivity.this);
            buider.setMessage("Are you sure you want to exit the application ?")
                    .setCancelable(false)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            finish();

                        }
                    });
            AlertDialog alert = buider.create();
            alert.show();

        } else {
            getSupportFragmentManager().popBackStack();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (item.getItemId() == R.id.action_cart) {
            Intent intent = new Intent(this, MyCartActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment f = null;

        if (id == R.id.nav_Cust_Account) {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user == null) {
                startActivityForResult(Config.FIREBASE_LOGIN(), FIREBASE_UI_SIGN_IN);
            } else {

                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                DatabaseReference dbUsersToken = FirebaseDatabase.getInstance().getReference();
                dbUsersToken.child("Paridiz").child("Customers").child(user.getUid()).child("device_token").setValue(deviceToken);

                f = new FragmentAccountInfo();

            }


            // Handle the camera action
        } else if (id == R.id.nav_Cust_Categories) {

            f = new FragmentCategories();
        } else if (id == R.id.nav_Cust_MyOrders) {

            Intent intent = new Intent(CustomerMainActivity.this, OrdersActivity.class);
            intent.putExtra("owner", false);
            startActivity(intent);

            //todo use here fragment

        } else if (id == R.id.nav_star) {

            Toast.makeText(this, "This feature is coming soon...", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_share) {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Paridiz");
            String share = "'My Fresh Mart Online Shopping App'\n\nShop daily needs products online with this amazing app" +
                    "\n\nDownload & Share app now...\n\n" +
                    "https://play.google.com/store/apps/details?id=com.my.fresh.mart" +
                    "";
            //todo add link for sharing app
            intent.putExtra(Intent.EXTRA_TEXT, share);
            startActivity(Intent.createChooser(intent, "Choose one"));

        } else if (id == R.id.nav_about) {
            //About Us
            Intent intent = new Intent(this, AboutUsActivity.class);
            startActivity(intent);
        }

        if (f != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right
            ).addToBackStack(Config.MAIN_STACK).replace(R.id.containerCustomerActivity, f).commit();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void CustLogout(MenuItem item) {


        Config.FIREBASE_LOGOUT(CustomerMainActivity.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }


    }

    public void patanjaliBaner(View view) {

        FragmentProductList ldf = new FragmentProductList();
        Bundle args = new Bundle();
        args.putString("CategoryValue", "Patanjali_Store");
        ldf.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right
        ).addToBackStack(Config.MAIN_STACK).replace(R.id.containerCustomerActivity, ldf).commit();


    }

    public void sweets(View view) {


        FragmentProductList ldf = new FragmentProductList();
        Bundle args = new Bundle();
        args.putString("CategoryValue", "Sweet_Mart");
        ldf.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right
        ).addToBackStack(Config.MAIN_STACK).replace(R.id.containerCustomerActivity, ldf).commit();

    }


    public void fruitsBanner(View view) {

        FragmentProductList ldf = new FragmentProductList();
        Bundle args = new Bundle();
        args.putString("CategoryValue", Config.FRUITS);
        ldf.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right
        ).addToBackStack(Config.MAIN_STACK).replace(R.id.containerCustomerActivity, ldf).commit();
    }

    public void dryFruitsBanner(View view) {

        FragmentProductList ldf = new FragmentProductList();
        Bundle args = new Bundle();
        args.putString("CategoryValue", Config.DRY_FRUITS);
        ldf.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right
        ).addToBackStack(Config.MAIN_STACK).replace(R.id.containerCustomerActivity, ldf).commit();
    }

    public void VegetablesBanner(View view) {

        FragmentProductList ldf = new FragmentProductList();
        Bundle args = new Bundle();
        args.putString("CategoryValue", Config.VEGETABLES);
        ldf.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right
        ).addToBackStack(Config.MAIN_STACK).replace(R.id.containerCustomerActivity, ldf).commit();
    }


//    public void openDialPlad(View view) {
//        Intent intent = new Intent(Intent.ACTION_DIAL);
//                                intent.setData(Uri.parse("tel:9730905340"));
//                                startActivity(intent);
//
//
//    }
//
//    public void openWhatsApp(View view) {
//        Intent sendIntent = new Intent();
//
//
//            Uri mUri = Uri.parse("smsto:9730905340");
//            Intent mIntent = new Intent(Intent.ACTION_SENDTO, mUri);
//        mIntent.putExtra(Intent.EXTRA_TEXT, "989");
////        mIntent.setType("text/plain");
//        mIntent.setPackage("com.whatsapp");
//
////            mIntent.putExtra("sms_body", "The text goes here");
////            mIntent.putExtra("chat",false);
//            startActivity(mIntent);
//
////                                sendIntent.setAction(Intent.ACTION_SENDTO);
////                                sendIntent.putExtra(Intent.EXTRA_TEXT, "989");
////                                sendIntent.setType("text/plain");
////                                sendIntent.setPackage("com.whatsapp");
////                                if (sendIntent.resolveActivity(getPackageManager()) != null) {
////                                    startActivity(sendIntent);
////                                }
//    }


    public void retriveFreomFirebaseAndSaveInSQLite() {

//        new AsyncTaskDownloadDB(CustomerMainActivity.this,t).execute();


//        LinkedList<String> categoryList = new LinkedList<>();
//        categoryList.add("Baby_products");
//        categoryList.add("Bakery_Dairy");
//        categoryList.add("Eletronics");
//        categoryList.add("Fruits_Dry_Fruits");
//        categoryList.add("Fruits_Vegetables");
//        categoryList.add("Grocery_Kirana");
//        categoryList.add("Household_Need");
//        categoryList.add("Patanjali_Store");
//        categoryList.add("Personal_Care");
//        categoryList.add("Snacks_Drink");
//        categoryList.add("Stationary");
//        categoryList.add("Street_products");
//        categoryList.add("Super_bazar");
//        categoryList.add("Sweet_Mart");
//
//        Iterator<String> listIterator=categoryList.iterator();
//
//
//        databaseHelper=new DatabaseHelper(this);
//        databaseHelper.getWritableDatabase();
//
//        StringBuffer stringBuffer=databaseHelper.getData();
//        t.setText(stringBuffer.toString());
//
//        while (listIterator.hasNext())
//        {
//            String cat=listIterator.next();
//            DatabaseReference dbrefs = FirebaseDatabase.getInstance().getReference();
//            dbrefs.child("Products").child(cat).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//
//                    for (DataSnapshot suggestiondataSnapshot : dataSnapshot.getChildren()) {
//                        String category = suggestiondataSnapshot.child("category").getValue(String.class);
//                        String name = suggestiondataSnapshot.child("title").getValue(String.class);
////                        t.append(count+") "+category+ "  :  "+name+"\n");
//                        count++;
////                        databaseHelper.savaData(category,name);
////                        Toast.makeText(MyCartActivity.this, ""+subCategoryOne, Toast.LENGTH_SHORT).show();
//
//                    }
//
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//
//        }
//        t.append("\n\n"+count);


    }

    public void searching() {
        databaseHelper = new DatabaseHelper(CustomerMainActivity.this);

        databaseHelper.getReadableDatabase();

//        Toolbar myToolBar = findViewById(R.id.toolbarSearchBar);
//        setSupportActionBar(myToolBar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setVoiceSearch(true);


        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(CustomerMainActivity.this, "Please click on the item", Toast.LENGTH_SHORT).show();
                //Do some magic
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                if (TextUtils.isEmpty(newText)) {
                    List<String> list = new ArrayList<>(databaseHelper.getSingleData());

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                            CustomerMainActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            android.R.id.text1,
                            list

                    );
                    searchView.setAdapter(arrayAdapter);
                }
                return true;
            }
        });

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Toast.makeText(CustomerMainActivity.this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();

                String s = parent.getItemAtPosition(position).toString();

                Fragment f = new FragmentProductList();
                Bundle args = new Bundle();
                args.putString("CategoryValue", databaseHelper.searchName(s));
                args.putString("searchQuery", s);
                f.setArguments(args);

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right
                ).addToBackStack(Config.MAIN_STACK).replace(R.id.containerCustomerActivity, f).commit();

                searchView.closeSearch();
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
    }

    public void loadNavigationHeader() {

        try {

            Config.ADDRESS.child(Config.FIREBASE_USER.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    try {
                        String name, mobileNo;
                        name = dataSnapshot.child("Name").getValue().toString();
                        tvName.setText(name);
                        mobileNo = dataSnapshot.child("MobileNo").getValue().toString();
                        tvNumber.setText(mobileNo);

                        tvName.setVisibility(View.VISIBLE);
                        tvNumber.setVisibility(View.VISIBLE);

                    } catch (Exception e) {

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            Log.d("CustomerParidizMain", e.toString());
        }

    }

}

