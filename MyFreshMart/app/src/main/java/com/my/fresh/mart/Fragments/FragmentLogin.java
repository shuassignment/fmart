package com.my.fresh.mart.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.my.fresh.mart.Config;
import com.my.fresh.mart.Model.Products;
import com.my.fresh.mart.ProductDescrpitionActivity;
import com.my.fresh.mart.R;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.HashMap;


import static com.my.fresh.mart.ParidizActivity.FIREBASE_UI_SIGN_IN;

/**
 * Created by intel on 04-Dec-17.
 */

public class FragmentLogin extends Fragment {

    public int buttonWidth;
    RecyclerView recyclerView;
    Query mQuery;
    ProgressDialog progressDialog;

    //Firebase
//    DatabaseReference dbAddToCart;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.products_recycler_view, container, false);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String category = "Patanjali_Store";

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        recyclerView = view.findViewById(R.id.rvProducts);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

//        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mDatabase = Config.PRODUCTS.child(category);

        mQuery = mDatabase.orderByKey();
//        mQuery = mDatabase.orderByChild("title");

        int total;
        try {


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
                    viewHolder.setTitle(model.getProductName());
//                    viewHolder.setDescription(model.getDescription());
                    viewHolder.setImage(getContext(), model.getImage());
//                    viewHolder.setPrice(model.getPrice());
                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), ProductDescrpitionActivity.class);
                            intent.putExtra("Title", model.getProductName());
//                            intent.putExtra("Description", model.getDescription());
//                            intent.putExtra("Price", model.getPrice());
                            intent.putExtra("Image", model.getImage());
                            intent.putExtra("ProductKey", ProductKey);
                            intent.putExtra("category", category);
//                            Toast.makeText(getActivity(), "This is" + model.getTitle(), Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }
                    });
//                    int total = +Integer.parseInt(model.getPrice());//todo
                    DatabaseReference dmDatabase = FirebaseDatabase.getInstance().getReference();

//                    Toast.makeText(getActivity(), "in rv", Toast.LENGTH_SHORT).show();
//                    dmDatabase.child("Total").setValue(total); todo
                    viewHolder.ibAddtoCart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                        Toast.makeText(ProductsPatanjaliStoreActivity.this, "This is " + model.getTitle(), Toast.LENGTH_SHORT).show();


                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user == null) {
                                startActivityForResult(
                                        AuthUI.getInstance()
                                                .createSignInIntentBuilder()
                                                .setAvailableProviders(
                                                        Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                                                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),

                                                                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),

                                                                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build())
                                                )
                                                .setIsSmartLockEnabled(false)

//                                                .setIsSmartLockEnabled(!BuildConfig.DEBUG)

                                                .build(), FIREBASE_UI_SIGN_IN);
                            } else {

                                DatabaseReference dbAddToCart = FirebaseDatabase.getInstance().getReference().child("Paridiz").child("Cart").child(user.getUid()).push();

                                final HashMap<String, String> cart = new HashMap<>();
//                                cart.put("description", model.getDescription());
                                cart.put("image", model.getImage());
//                                cart.put("price", model.getPrice());
//                                cart.put("title", model.getTitle());
                                cart.put("quantity", "1");
                                cart.put("item_key", ProductKey);
                                final DatabaseReference dbAddToCarts = FirebaseDatabase.getInstance().getReference().child("Paridiz").child("Cart").child(user.getUid());
                                dbAddToCarts.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (!dataSnapshot.hasChild(ProductKey)) {
//                                        dbAddToCarts.child(ProductKey).child("quantity").setValue("1");
                                            Toast.makeText(getActivity(), "Product not exist", Toast.LENGTH_SHORT).show();

                                        } else {
                                            int a = Integer.parseInt(dataSnapshot.child(ProductKey).child("quantity").getValue().toString());
                                            Toast.makeText(getActivity(), "value is :" + a, Toast.LENGTH_SHORT).show();
                                            int b = a + 1;
                                            dbAddToCarts.child(ProductKey).child("quantity").setValue("" + b);
                                            Toast.makeText(getActivity(), "Product already exists", Toast.LENGTH_SHORT).show();

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                                dbAddToCarts.child(ProductKey).setValue(cart);
                                Toast.makeText(getActivity(), "Product added to cart", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.blink);
                    viewHolder.ibAddtoCart.startAnimation(animation);
                    viewHolder.ibAddtoCart.startAnimation(animation);
                }
            };
            recyclerView.setAdapter(firebaseRecyclerAdapter);
//            Toast.makeText(getActivity(), "try end", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();
        }
//        Toast.makeText(getActivity(), "end", Toast.LENGTH_SHORT).show();


    }

    public static class ProductsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        Button ibAddtoCart;

        public ProductsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            ibAddtoCart = mView.findViewById(R.id.btnAddToCart);

        }


        public void setTitle(String title) {
            TextView ProductTitle = mView.findViewById(R.id.productTitle);

            ProductTitle.setText(title);
        }

        public void setDescription(String description) {
            TextView ProductDescrition = mView.findViewById(R.id.productDescrition);
            ProductDescrition.setText(description);
        }

        public void setImage(Context ctx, String image) {
            ImageView imageView = mView.findViewById(R.id.productImage);
            Picasso.with(ctx).load(image).placeholder(R.drawable.placehoder_image).into(imageView);

        }

        public void setPrice(String price) {
            TextView ProductPrice = mView.findViewById(R.id.tvPrice);
            ProductPrice.setText("₹ " + price);

        }
    }


}
