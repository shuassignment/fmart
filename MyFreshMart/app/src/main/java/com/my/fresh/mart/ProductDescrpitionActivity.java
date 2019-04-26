package com.my.fresh.mart;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.my.fresh.mart.Model.Products;

import java.util.HashMap;

import static com.my.fresh.mart.ParidizActivity.FIREBASE_UI_SIGN_IN;

public class ProductDescrpitionActivity extends AppCompatActivity {

    Bundle data;
    TextView tvTitle, tvRemark, tvPrice;

    PhotoView ivProductImage;

    String amount;
    private DatabaseReference mDatabase;
    //Firebase

    Query mQuery;

    private FirebaseAuth mAuth;
    String ProductName, Remark, OfferPrice,MRPPrice,Weight, Image, ProductKey,category;
    int width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_descrpition);

        //screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        mAuth = FirebaseAuth.getInstance();

        data = getIntent().getExtras();
        category = data.getString("Category");


        ProductKey = data.getString("ProductKey");





        //todo similar products
//        recyclerView = findViewById(R.id.rvProductDescriptionRelated);


        GridLayoutManager gridLayoutManager=new GridLayoutManager(ProductDescrpitionActivity.this,2,GridLayoutManager.VERTICAL,false);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

//        recyclerView.setLayoutManager(layoutManager);



        ////////////***********************

        try {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Products").child(category);
        }catch (Exception e){

        }


        mQuery = mDatabase.orderByChild("title").startAt("A").limitToFirst(20);

        Log.d("key: ",ProductKey);

        FirebaseRecyclerAdapter<Products, ProductsPatanjaliStoreActivity.ProductsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Products, ProductsPatanjaliStoreActivity.ProductsViewHolder>(
                Products.class,
                R.layout.products,
                ProductsPatanjaliStoreActivity.ProductsViewHolder.class,
                mQuery
        ) {
            @Override
            protected void populateViewHolder(ProductsPatanjaliStoreActivity.ProductsViewHolder viewHolder, final Products model, int position) {

                int margin= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());

                CardView.LayoutParams params = new CardView.LayoutParams(
                        (width/2)-(margin*4),
                        CardView.LayoutParams.MATCH_PARENT
                );
                params.setMargins(margin, margin, margin, margin);

                viewHolder.productView.setLayoutParams(params);

                final String ProductKey = getRef(position).getKey();
                viewHolder.setProductName(model.getProductName());
//                viewHolder.setDescription(model.getDescription());
                viewHolder.setImage(getApplicationContext(), model.getImage());
//                viewHolder.setPrice(model.getPrice());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ProductDescrpitionActivity.this, ProductDescrpitionActivity.class);
                        intent.putExtra("Title", model.getProductName());
//                        intent.putExtra("Description", model.getDescription());
//                        intent.putExtra("Price", model.getPrice());
                        intent.putExtra("Image", model.getImage());
                        intent.putExtra("ProductKey", ProductKey);
                        intent.putExtra("category", category);
//                        intent.putExtra("delivery",model.getDeliveryCharge());

                        startActivity(intent);
                    }
                });
//                int total=+Integer.parseInt(model.getPrice());todo
                DatabaseReference dmDatabase=FirebaseDatabase.getInstance().getReference();
//                dmDatabase.child("Total").setValue(total);//todo

                viewHolder.ibAddtoCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {


                        //todo add product immediate after login
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user == null) {
                            startActivityForResult(Config.FIREBASE_LOGIN(), FIREBASE_UI_SIGN_IN);
                        }
                        else {



                            final HashMap<String,Object> cart=Config.CartMap(ProductKey,model.getImage(),model.getProductName(),"","","","1",category,Config.getCurrentTime());

//                            final DatabaseReference dbAddToCarts = FirebaseDatabase.getInstance().getReference().child("Paridiz").child("Cart").child(user.getUid());
                            Config.CART.child(Config.FIREBASE_USER.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (!dataSnapshot.hasChild(ProductKey))
                                    {
                                        Config.CART.child(Config.FIREBASE_USER.getUid()).child(ProductKey).setValue(cart).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(view, "Product added to cart", Snackbar.LENGTH_LONG).show();
                                            }
                                        });

                                    }
                                    else {
                                        Snackbar.make(view, "Product already exists", Snackbar.LENGTH_LONG).show();

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }

                    }
                });

                Animation animation = AnimationUtils.loadAnimation(getApplication(), R.anim.blink);
                viewHolder.ibAddtoCart.startAnimation(animation);
                viewHolder.ibAddtoCart.startAnimation(animation);
            }
        };
//        recyclerView.setAdapter(firebaseRecyclerAdapter);


        tvTitle = findViewById(R.id.tvProductName);
        tvRemark = findViewById(R.id.tvRemark);
        tvPrice = findViewById(R.id.tvPriceWeight);
        ivProductImage = findViewById(R.id.ivProductImage);

        ProductName = data.getString("ProductName");
        Remark = data.getString("Remark");
        OfferPrice= data.getString("OfferPrice");
        MRPPrice= data.getString("MRPPrice");
        Weight= data.getString("Weight");
        Image = data.getString("Image");
        tvTitle.setText(ProductName);
        tvRemark.setText(Remark);
        String price="MRP : <strike> ₹ " + MRPPrice+ " </strike> <b> ₹"+OfferPrice+ "</b>";
        tvPrice.setText(Html.fromHtml(price));
//        tvDelivery = findViewById(R.id.tvProductDescrpitionActivityProductDeliveryCharge);
//        tvDelivery.setText("Delivery Charges : ₹ "+deliveryCharges);
        //Picasso.with(getApplicationContext()).load(Image).placeholder(R.drawable.placehoder_image).into(ivProductImage);
        Glide.with(this)
                .load(Image)
                .placeholder(R.drawable.placehoder_image)
                .into(ivProductImage);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_cart) {
            Intent intent = new Intent(ProductDescrpitionActivity.this, MyCartActivity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.action_search) {
            Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void AddProductToCart(final View view) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivityForResult(Config.FIREBASE_LOGIN(), FIREBASE_UI_SIGN_IN);
        } else {

          final HashMap<String, Object> cart = Config.CartMap(ProductKey,Image,ProductName,OfferPrice,MRPPrice,Weight,"1",category,Config.getCurrentTime());

            final DatabaseReference dbAddToCarts = FirebaseDatabase.getInstance().getReference().child("Paridiz").child("Cart").child(user.getUid());
            dbAddToCarts.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(ProductKey)) {
                        dbAddToCarts.child(ProductKey).setValue(cart).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(view, "Product added to cart", Snackbar.LENGTH_LONG).show();
                            }
                        });

                    } else {
                        Snackbar.make(view, "Product already exists", Snackbar.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
        }
    }

    public void buynow(View view) {

        Intent intent = new Intent(ProductDescrpitionActivity.this, CheckoutActivity.class);
        intent.putExtra("OrderTotal", "" + amount);
        startActivity(intent);

    }
}
