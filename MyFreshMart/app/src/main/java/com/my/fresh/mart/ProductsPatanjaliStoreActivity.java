package com.my.fresh.mart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

public class ProductsPatanjaliStoreActivity extends AppCompatActivity {
    public int buttonWidth;
    RecyclerView recyclerView;
    Query mQuery;
    ProgressDialog progressDialog;
    public Boolean admin;

    //Firebase
    //DatabaseReference dbAddToCart;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    Button btnSort, btnFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.products_recycler_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle data = getIntent().getExtras();
        admin = false;
        admin = data.getBoolean("admin");

        final String category = data.getString("CategoryValue");

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        try {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Products").child(category);

        }catch (Exception e){

        }
        mQuery = mDatabase.orderByChild("all");





        recyclerView = findViewById(R.id.rvProducts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));


        FirebaseRecyclerAdapter<Products, ProductsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Products, ProductsViewHolder>(
                Products.class,
                R.layout.products,
                ProductsViewHolder.class,
                mQuery
        ) {
            @Override
            protected void populateViewHolder(ProductsViewHolder viewHolder, final Products model, int position) {
                progressDialog.dismiss();
                final String ProductKey = getRef(position).getKey();
                viewHolder.setProductName(model.getProductName());
                if (admin) {
                    viewHolder.ibAdminDeleteProduct.setVisibility(View.VISIBLE);
                    viewHolder.ibAddtoCart.setVisibility(View.GONE);
                    viewHolder.ibAdminDeleteProduct.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            final AlertDialog.Builder builder = new AlertDialog.Builder(ProductsPatanjaliStoreActivity.this);
                            builder.setTitle("Are you sure you want to delete this product")
                                    .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            mDatabase.child(ProductKey).removeValue();


                                        }
                                    });
                            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    dialogInterface.dismiss();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }
                    });
                    viewHolder.ibAdminEditProduct.setVisibility(View.VISIBLE);

                    viewHolder.ibAdminEditProduct.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(ProductsPatanjaliStoreActivity.this, MainActivity.class);

                            intent.putExtra("editProductDetails", true);
                            intent.putExtra("editProductName", model.getProductName());
                            intent.putExtra("productKey", ProductKey);
                            intent.putExtra("category", category);
//                            intent.putExtra("editProductDescription", model.getDescription());
                            intent.putExtra("Image_Path", model.getImage());
//                            intent.putExtra("editProductDelivery", model.getDeliveryCharge());
//                            intent.putExtra("editProductPrice", model.getPrice());
//                            intent.putExtra("editProductCategory", model.getCategory());//todo getCategory  **imp


                            intent.putExtra("editProductKey", ProductKey);

                            //todo uncomment startActivity and do logic for
                            Toast.makeText(ProductsPatanjaliStoreActivity.this, "This feature is comming soon", Toast.LENGTH_SHORT).show();
                            startActivity(intent);


                        }
                    });
                } else {
                    viewHolder.ibAdminDeleteProduct.setVisibility(View.GONE);
        }

//                viewHolder.setDescription(model.getDescription());
                viewHolder.setImage(getApplicationContext(), model.getImage());
//                viewHolder.setPrice(model.getPrice());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ProductsPatanjaliStoreActivity.this, ProductDescrpitionActivity.class);
                        intent.putExtra("Title", model.getProductName());
//                        intent.putExtra("Description", model.getDescription());
//                        intent.putExtra("Price", model.getPrice());
                        intent.putExtra("Image", model.getImage());
                        intent.putExtra("ProductKey", ProductKey);
                        intent.putExtra("category", category);
//                        intent.putExtra("delivery", model.getDeliveryCharge());
                //TODO  start activity uncomment
//                        startActivity(intent);
                    }
                });

                viewHolder.ibAddtoCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user == null) {

                            startActivityForResult(Config.FIREBASE_LOGIN(), FIREBASE_UI_SIGN_IN);
                        } else {


                            final HashMap<String, String> cart = new HashMap<>();
//                            cart.put("description", model.getDescription());
                            cart.put("image", model.getImage());
//                            cart.put("price", model.getPrice());
                            cart.put("title", model.getProductName());
                            cart.put("quantity", "1");
                            cart.put("item_key", ProductKey);
                            final DatabaseReference dbAddToCarts = FirebaseDatabase.getInstance().getReference().child("Paridiz").child("Cart").child(user.getUid());
                            dbAddToCarts.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (!dataSnapshot.hasChild(ProductKey)) {
//                                        dbAddToCarts.child(ProductKey).child("quantity").setValue("1");
//                                        Toast.makeText(ProductsPatanjaliStoreActivity.this, "Product not exist", Toast.LENGTH_SHORT).show();

                                    } else {

                                        Toast.makeText(ProductsPatanjaliStoreActivity.this, "Product already exists", Toast.LENGTH_SHORT).show();

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                            dbAddToCarts.child(ProductKey).setValue(cart);
                            Toast.makeText(ProductsPatanjaliStoreActivity.this, "Product added to cart", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                Animation animation = AnimationUtils.loadAnimation(getApplication(), R.anim.blink);
                viewHolder.ibAddtoCart.startAnimation(animation);
                viewHolder.ibAddtoCart.startAnimation(animation);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
//        Toast.makeText(this, "Total is :" +total, Toast.LENGTH_SHORT).show();
    }

    public static class ProductsViewHolder extends RecyclerView.ViewHolder {

        public View mView;

        public CardView productView;
        public Button ibAddtoCart;
        public ImageButton ibAdminDeleteProduct, ibAdminEditProduct;


        public ProductsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            ibAddtoCart = mView.findViewById(R.id.btnAddToCart);
            ibAdminDeleteProduct = mView.findViewById(R.id.adminDeleteProduct);
            ibAdminEditProduct = mView.findViewById(R.id.adminEditProduct);

            ibAdminEditProduct.setVisibility(View.GONE);
            ibAdminDeleteProduct.setVisibility(View.GONE);
            productView=mView.findViewById(R.id.productView);

        }


        public void setProductName(String Name) {
            TextView ProductName= mView.findViewById(R.id.productTitle);

            ProductName.setText(Name);
        }

        public void setDescription(String description) {
            TextView ProductDescrition = mView.findViewById(R.id.productDescrition);
            ProductDescrition.setText(description);
        }

        public void setImage(Context ctx, String image) {
            ImageView imageView = mView.findViewById(R.id.productImage);
//            Picasso.with(ctx).load(image).placeholder(R.drawable.placehoder_image).into(imageView);
            Glide.with(ctx)
                    .load(image)
                    .placeholder(R.drawable.placehoder_image)
                    .into(imageView);

        }

        public void setPrice(String price) {
            TextView ProductPrice = mView.findViewById(R.id.tvPrice);
            ProductPrice.setText("₹ " + price);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_cart) {
            Intent intent = new Intent(ProductsPatanjaliStoreActivity.this, MyCartActivity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.action_search) {
            Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
