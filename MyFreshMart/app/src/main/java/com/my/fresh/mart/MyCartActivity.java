package com.my.fresh.mart;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.util.Log;
import android.view.View;
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
import com.my.fresh.mart.Model.CartProducts;

import static com.my.fresh.mart.ParidizActivity.FIREBASE_UI_SIGN_IN;

public class MyCartActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    String uid;
    RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    Query mQuery;
    Button btnCheckout;
    ProgressDialog progressDialog;

    int total = 0;
    TextView tvTotalAmount,tvMinCartValue;
    String minValue;
    String minCartValue="100";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences prefs = getSharedPreferences(Config.SHARED_PREFERENCE_TNC, MODE_PRIVATE);

        minCartValue = prefs.getString("MinCartValue", null);


        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvMinCartValue=findViewById(R.id.tvMinCartValue);


        minValue = "Min Cart value to checkout is ₹ "+minCartValue;
        tvMinCartValue.setText(minValue);
        btnCheckout=findViewById(R.id.btnCheckout);
        progressDialog = new ProgressDialog(this);

        progressDialog.setCancelable(false);
        progressDialog.show();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            progressDialog.dismiss();
            startActivityForResult(Config.FIREBASE_LOGIN(), FIREBASE_UI_SIGN_IN);

            finish();
        } else {


            try {

                DatabaseReference dbrefs = FirebaseDatabase.getInstance().getReference();
                Config.CART.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int b = 0;

                        for (DataSnapshot suggestiondataSnapshot : dataSnapshot.getChildren()) {
                            String OfferPrice = suggestiondataSnapshot.child("OfferPrice").getValue(String.class);
                            String Quantity = suggestiondataSnapshot.child("Quantity").getValue(String.class);
                            //String delivery = suggestiondataSnapshot.child("delivery").getValue(String.class);
                            int mul = Integer.parseInt(OfferPrice) * Integer.parseInt(Quantity);
                            int a = mul;//+Integer.parseInt(delivery);
                            b = a + b;
                        }
                        if(b<Integer.parseInt(minCartValue)){
                            btnCheckout.setVisibility(View.INVISIBLE);
                            btnCheckout.setEnabled(false);

                        }else{
                            btnCheckout.setEnabled(true);
                            btnCheckout.setVisibility(View.VISIBLE);
                        }
                        tvTotalAmount.setText("Total  =  ₹ " + b);

                        total = b;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            } catch (Exception e) {
                tvTotalAmount.setText("" + e);
                Log.d("err", "" + e);
            }
            //***************************


            uid = "" + mAuth.getCurrentUser().getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Paridiz").child("Cart");
            recyclerView = findViewById(R.id.rvMyCart);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            final DatabaseReference mDatabaseP = FirebaseDatabase.getInstance().getReference().child("Paridiz").child("Cart").child(uid);
            mQuery = mDatabaseP.orderByKey();


            mQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null) {

                        TextView textView=findViewById(R.id.tvMinCartValue);
                        TextView tvTotalAmount=findViewById(R.id.tvTotalAmount);
                        textView.setText(R.string.empty_cart);
                        textView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        btnCheckout.setVisibility(View.GONE);
                        tvTotalAmount.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            FirebaseRecyclerAdapter<CartProducts, ProductsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CartProducts, ProductsViewHolder>(
                    CartProducts.class,
                    R.layout.cart_products,
                    ProductsViewHolder.class,
                    mQuery
            ) {
                @Override
                protected void populateViewHolder(final ProductsViewHolder viewHolder, final CartProducts model, int position) {

                    final String MyCartKey = getRef(position).getKey();

                    viewHolder.setProductName(model.getProductName());
                    viewHolder.setWeight(model.getWeight());
                    viewHolder.setImage(getApplicationContext(), model.getImage());
                    viewHolder.setPrice("MRP : ₹ <strike> "+model.getMRP()+" </strike>\t<b>"+model.getOfferPrice()+"</b>");
                    viewHolder.setQuantity(model.getQuantity());


//                    totalAmount[0] +=Integer.parseInt(model.getPrice());
//                    tvTotalAmount.setText(""+ totalAmount[0]);
                    viewHolder.btnRemoveFromCart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            AlertDialog.Builder DialogRemove=new AlertDialog.Builder(MyCartActivity.this);

                            DialogRemove
                            .setMessage("Are you sure you want to Remove Product from Cart ?")
                            .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mDatabaseP.child(MyCartKey).removeValue();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });

                            AlertDialog alertDialog=DialogRemove.create();
                            alertDialog.show();



                        }
                    });

                    viewHolder.btnIncreaseQty.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            mDatabaseP.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    int a = Integer.parseInt(dataSnapshot.child(MyCartKey).child("Quantity").getValue().toString());
                                    int b = a + 1;
                                    mDatabaseP.child(MyCartKey).child("Quantity").setValue("" + b);
                                    viewHolder.btnDecreaseQty.setVisibility(View.VISIBLE);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    });


                    viewHolder.btnDecreaseQty.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            mDatabaseP.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    int Quantity = Integer.parseInt(dataSnapshot.child(MyCartKey).child("Quantity").getValue().toString());

                                    if (Quantity > 1) {
                                        viewHolder.btnDecreaseQty.setVisibility(View.VISIBLE);
                                        int b = Quantity - 1;
                                        mDatabaseP.child(MyCartKey).child("Quantity").setValue("" + b);
                                    } else {
                                        Toast.makeText(MyCartActivity.this, "Quantity is 1", Toast.LENGTH_SHORT).show();
                                        viewHolder.btnDecreaseQty.setVisibility(View.GONE);
                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            };
            recyclerView.setAdapter(firebaseRecyclerAdapter);
            SwipeController swipeController = new SwipeController();
            ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
            itemTouchhelper.attachToRecyclerView(recyclerView);

            progressDialog.dismiss();
        }

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyCartActivity.this, CheckoutActivity.class);
                intent.putExtra("OrderTotal", "" + total);
                startActivity(intent);
            }
        });

    }


    public void Checkout(View view) {
        Intent intent = new Intent(MyCartActivity.this, CheckoutActivity.class);
        intent.putExtra("OrderTotal", "" + total);
        startActivity(intent);
    }


    public static class ProductsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageButton btnRemoveFromCart, btnIncreaseQty, btnDecreaseQty;
        TextView tvQuantity;

        public ProductsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            btnRemoveFromCart = mView.findViewById(R.id.btnMyCartRemoveFromCart);
            btnIncreaseQty = mView.findViewById(R.id.ibIncreaseQuantity);
            btnDecreaseQty = mView.findViewById(R.id.ibDecreaseQuantity);
        }

        public void setProductName(String Name) {
            TextView ProductName = mView.findViewById(R.id.tvProductName);
            ProductName.setText(Name);
        }

        public void setWeight(String weight) {
            TextView ProductWeight = mView.findViewById(R.id.tvProductWeight);
            ProductWeight.setText(weight);
        }

        public void setImage(Context ctx, String image) {
            ImageView imageView = mView.findViewById(R.id.ivMyCartproductImage);
//            Picasso.with(ctx).load(image).placeholder(R.drawable.placehoder_image).into(imageView);

            Glide.with(ctx)
                    .load(image)
                    .placeholder(R.drawable.placehoder_image)
                    .into(imageView);
        }

        public void setPrice(String price) {
            TextView ProductPrice = mView.findViewById(R.id.tvProductPrice);
            ProductPrice.setText(Html.fromHtml(price));

        }

        public void setQuantity(String Quantity) {
            tvQuantity = mView.findViewById(R.id.tvQuantity);
            tvQuantity.setText(Quantity);

        }


    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
