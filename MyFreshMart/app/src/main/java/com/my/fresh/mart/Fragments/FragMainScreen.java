package com.my.fresh.mart.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.my.fresh.mart.Config;
import com.my.fresh.mart.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

/**
 * Created by intel on 22-Mar-18.
 */

public class FragMainScreen extends Fragment {

    ImageView  bannerFruits,bannerVegetables,bannerDryFruits;
    LinearLayout linearLayout;
    FrameLayout frameLayout;

    RecyclerView recyclerView;


    //    Firebase
    Query mQuery, mQuery2, mQuery3;
    FirebaseUser user;
    DatabaseReference mDatabase, mDatabase2, mDatabase3;

    int width;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_main_screen, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        try {
            userInfo();
        }catch (Exception e){
            e.printStackTrace();
        }


        final TextView tvOffer1,tvOffer2,tvOffer3;
        tvOffer1=view.findViewById(R.id.tvOffer1);
        tvOffer1.setVisibility(View.GONE);
        tvOffer2=view.findViewById(R.id.tvOffer2);
        tvOffer2.setVisibility(View.GONE);
        tvOffer3=view.findViewById(R.id.tvOffer3);
        tvOffer3.setVisibility(View.GONE);

        Config.PARIDIZ.child("Offers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String offer1 = null,offer2 = null,offer3 = null; 
                try {
                    try {
                        offer1 = dataSnapshot.child("Offer1").getValue().toString();
                    }catch (Exception e){

                    }

                    try {
                        offer2 = dataSnapshot.child("Offer2").getValue().toString();
                    }catch (Exception e){

                    }

                    try {
                        offer3 = dataSnapshot.child("Offer3").getValue().toString();
                    }catch (Exception e){

                    }
                }catch(Exception e){
//                    Toast.makeText(getActivity(), ""+e, Toast.LENGTH_SHORT).show();
                }
                if (offer1==null || offer1.equals("")){
                    tvOffer1.setVisibility(View.GONE);
                }else {
                    tvOffer1.setVisibility(View.VISIBLE);
                    tvOffer1.setText(offer1);
                }
                if (offer2==null || offer2.equals("")){
                    tvOffer2.setVisibility(View.GONE);
                }else {
                    tvOffer2.setVisibility(View.VISIBLE);
                    tvOffer2.setText(offer2);
                }
                if (offer3==null || offer3.equals("")){
                    tvOffer3.setVisibility(View.GONE);
                }else {
                    tvOffer3.setVisibility(View.VISIBLE);
                    tvOffer3.setText(offer3);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        frameLayout = view.findViewById(R.id.containerCustomerActivity);

        bannerFruits = view.findViewById(R.id.ivBannerFruits);

        Picasso.with(getActivity()).load("https://freepngimg.com/thumb/fruit/6-2-fruit-png-picture.png").into(bannerFruits);
//        Picasso.with(getActivity()).load("https://firebasestorage.googleapis.com/v0/b/my-fresh-mart.appspot.com/o/Banner%2Ffruit_picture.png?alt=media&token=2ec4c8be-c06b-4de4-9158-6f3b81e05e87").into(bannerFruits);


        bannerVegetables = view.findViewById(R.id.ivBannerVegetables);

//        Picasso.with(getActivity()).load("https://firebasestorage.googleapis.com/v0/b/my-fresh-mart.appspot.com/o/Banner%2Fvegetable_picture.png?alt=media&token=61019570-49b5-4665-9341-a81b14b3c678").into(bannerVegetables);
        Picasso.with(getActivity()).load("https://freepngimg.com/thumb/vegetable/1-2-vegetable-free-png-image.png").into(bannerVegetables);

        bannerDryFruits = view.findViewById(R.id.ivBannerDryFruits);

        Picasso.with(getActivity()).load("https://d1iqctulejj45h.cloudfront.net/0025154.png").into(bannerDryFruits);


        linearLayout = view.findViewById(R.id.custLayout);

        // first horizontal recycler

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        String qry = "";
        switch (day) {
            case Calendar.SUNDAY:
                qry="A";
                break;
            case Calendar.MONDAY:
                qry="D";
                break;
            case Calendar.TUESDAY:
                qry="G";
                break;
            case Calendar.WEDNESDAY:
                qry="K";
                break;
            case Calendar.THURSDAY:
                qry="O";
                break;
            case Calendar.FRIDAY:
                qry="P";
                break;
            case Calendar.SATURDAY:
                qry="Q";
                break;
        }

/*
        recyclerView = view.findViewById(R.id.rvSuperBazar);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(layoutManager);



        mDatabase = FirebaseDatabase.getInstance().getReference().child("Products").child(Config.FRUITS);

        //todo uncomment query
        mQuery= Config.DB_FRUITS.orderByChild("title");//.startAt(qry).limitToFirst(15);

        FirebaseRecyclerAdapter<Products, ProductsPatanjaliStoreActivity.ProductsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Products, ProductsPatanjaliStoreActivity.ProductsViewHolder>(
                Products.class,
                R.layout.products,
                ProductsPatanjaliStoreActivity.ProductsViewHolder.class,
                mQuery
        ) {
            @Override
            protected void populateViewHolder(ProductsPatanjaliStoreActivity.ProductsViewHolder viewHolder, final Products model, int position) {

                int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());

                CardView.LayoutParams params = new CardView.LayoutParams(
                        (width / 2) - (margin * 4),
                        CardView.LayoutParams.MATCH_PARENT
                );
                params.setMargins(margin, margin, margin, margin);

                viewHolder.productView.setLayoutParams(params);

                final String ProductKey = getRef(position).getKey();
                viewHolder.setProductName(model.getProductName());
//                viewHolder.setDescription(model.getDescription());
                viewHolder.setImage(getActivity(), model.getImage());
//                viewHolder.setPrice(model.getPrice());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ProductDescrpitionActivity.class);
                        intent.putExtra("Title", model.getProductName());
//                        intent.putExtra("Description", model.getDescription());
//                        intent.putExtra("Price", model.getPrice());
                        intent.putExtra("Image", model.getImage());
                        intent.putExtra("ProductKey", ProductKey);
                        intent.putExtra("category", "Super_bazar");
//                        intent.putExtra("delivery", model.getDeliveryCharge());

                        startActivity(intent);
                    }
                });

                viewHolder.ibAddtoCart.setVisibility(View.GONE);
                viewHolder.ibAddtoCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {


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
                            cart.put("category", "Super_bazar");
                            cart.put("item_key", ProductKey);
//                            cart.put("delivery", model.getDeliveryCharge());

                            final DatabaseReference dbAddToCarts = FirebaseDatabase.getInstance().getReference().child("Paridiz").child("Cart").child(user.getUid());
                            dbAddToCarts.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (!dataSnapshot.hasChild(ProductKey)) {
                                        dbAddToCarts.child(ProductKey).setValue(cart);
                                        Snackbar.make(view, "Product added to cart", Snackbar.LENGTH_LONG).show();

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
                });

                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.blink);
                viewHolder.ibAddtoCart.startAnimation(animation);
                viewHolder.ibAddtoCart.startAnimation(animation);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);

        // Second horizontal recycler

        RecyclerView recyclerView2 = view.findViewById(R.id.rvPatanjaliStore);
        recyclerView2.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManager2
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView2.setLayoutManager(layoutManager2);


        mDatabase2 = FirebaseDatabase.getInstance().getReference().child("Products").child(Config.VEGETABLES);


        mQuery2 = Config.DB_VEGETABLES.orderByChild("title");//.startAt(qry);//.limitToFirst(15);

        FirebaseRecyclerAdapter<Products, ProductsPatanjaliStoreActivity.ProductsViewHolder> firebaseRecyclerAdapter2 = new FirebaseRecyclerAdapter<Products, ProductsPatanjaliStoreActivity.ProductsViewHolder>(
                Products.class,
                R.layout.products,
                ProductsPatanjaliStoreActivity.ProductsViewHolder.class,
                mQuery2
        ) {
            @Override
            protected void populateViewHolder(ProductsPatanjaliStoreActivity.ProductsViewHolder viewHolder, final Products model, int position) {

                int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());

                CardView.LayoutParams params = new CardView.LayoutParams(
                        (width / 2) - (margin * 4),
                        CardView.LayoutParams.MATCH_PARENT
                );
                params.setMargins(margin, margin, margin, margin);

                viewHolder.productView.setLayoutParams(params);


                final String ProductKey = getRef(position).getKey();
                viewHolder.setProductName(model.getProductName());
//                viewHolder.setDescription(model.getDescription());
                viewHolder.setImage(getActivity(), model.getImage());
//                viewHolder.setPrice(model.getPrice());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ProductDescrpitionActivity.class);
                        intent.putExtra("Title", model.getProductName());
//                        intent.putExtra("Description", model.getDescription());
//                        intent.putExtra("Price", model.getPrice());
                        intent.putExtra("Image", model.getImage());
                        intent.putExtra("ProductKey", ProductKey);
                        intent.putExtra("category", "Patanjali_Store");
//                        intent.putExtra("delivery", model.getDeliveryCharge());

                        startActivity(intent);
                    }
                });

                viewHolder.ibAddtoCart.setVisibility(View.GONE);
                viewHolder.ibAddtoCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {


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
                            cart.put("category", "Patanjali_Store");
                            cart.put("item_key", ProductKey);
//                            cart.put("delivery", model.getDeliveryCharge());

                            final DatabaseReference dbAddToCarts = FirebaseDatabase.getInstance().getReference().child("Paridiz").child("Cart").child(user.getUid());
                            dbAddToCarts.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (!dataSnapshot.hasChild(ProductKey)) {
                                        dbAddToCarts.child(ProductKey).setValue(cart);
                                        Snackbar.make(view, "Product  added to cart", Snackbar.LENGTH_LONG).show();

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
                });

                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.blink);
                viewHolder.ibAddtoCart.startAnimation(animation);
                viewHolder.ibAddtoCart.startAnimation(animation);
            }
        };
        recyclerView2.setAdapter(firebaseRecyclerAdapter2);


        // first horizontal recycler

        RecyclerView recyclerView3 = view.findViewById(R.id.rvFruitsVegetables);
        recyclerView3.setNestedScrollingEnabled(false);


        LinearLayoutManager layoutManager3
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView3.setLayoutManager(layoutManager3);


        mDatabase3 = FirebaseDatabase.getInstance().getReference().child("Products").child(Config.DRY_FRUITS);

        mQuery3 = Config.DB_DRY_FRUITS.orderByChild("title");//.startAt(qry).limitToFirst(15);

        FirebaseRecyclerAdapter<Products, ProductsPatanjaliStoreActivity.ProductsViewHolder> firebaseRecyclerAdapter3 = new FirebaseRecyclerAdapter<Products, ProductsPatanjaliStoreActivity.ProductsViewHolder>(
                Products.class,
                R.layout.products,
                ProductsPatanjaliStoreActivity.ProductsViewHolder.class,
                mQuery3
        ) {
            @Override
            protected void populateViewHolder(final ProductsPatanjaliStoreActivity.ProductsViewHolder viewHolder, final Products model, int position) {

                int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());

                CardView.LayoutParams params = new CardView.LayoutParams(
                        (width / 2) - (margin * 4),
                        CardView.LayoutParams.MATCH_PARENT
                );
                params.setMargins(margin, margin, margin, margin);

                viewHolder.productView.setLayoutParams(params);


                final String ProductKey = getRef(position).getKey();
                viewHolder.setProductName(model.getProductName());
//                viewHolder.setDescription(model.getDescription());
                viewHolder.setImage(getActivity(), model.getImage());
//                viewHolder.setPrice(model.getPrice());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Intent intent = new Intent(getActivity(), ProductDescrpitionActivity.class);
                        intent.putExtra("Title", model.getProductName());
//                        intent.putExtra("Description", model.getDescription());
//                        intent.putExtra("Price", model.getPrice());
                        intent.putExtra("Image", model.getImage());
                        intent.putExtra("ProductKey", ProductKey);
                        intent.putExtra("category", "Fruits_Vegetables");
//                        intent.putExtra("delivery", model.getDeliveryCharge());

                        startActivity(intent);
                    }
                });

                viewHolder.ibAddtoCart.setVisibility(View.GONE);
                viewHolder.ibAddtoCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {


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
                            cart.put("category", "Fruits_Vegetables");
                            cart.put("item_key", ProductKey);
//                            cart.put("delivery", model.getDeliveryCharge());

                            final DatabaseReference dbAddToCarts = FirebaseDatabase.getInstance().getReference().child("Paridiz").child("Cart").child(user.getUid());
                            dbAddToCarts.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (!dataSnapshot.hasChild(ProductKey)) {
                                        dbAddToCarts.child(ProductKey).setValue(cart);
                                        Snackbar.make(view, "Product added to cart", Snackbar.LENGTH_LONG).show();

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
                });

                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.blink);
                viewHolder.ibAddtoCart.startAnimation(animation);
                viewHolder.ibAddtoCart.startAnimation(animation);
            }
        };
//        recyclerView3.setAdapter(firebaseRecyclerAdapter3);
        recyclerView3.setVisibility(View.GONE);

        */
    }

    void userInfo()
    {
 /*       user=Config.FIREBASE_USER;
        SharedPreferences UserToken = getActivity().getSharedPreferences(Config.NUMBER_OF_TIMES_APP_OPEN, 0); // 0 - for private mode
        int Count = UserToken.getInt("COUNT", 0);
        String First_opended=UserToken.getString("First_opended",null);

        try {
            String unique_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

            HashMap<String, String> device_information = new HashMap<>();
            device_information.put("DEVICE_ID", unique_id);
            device_information.put("DEVICE_MODEL", android.os.Build.MODEL);
            device_information.put("VERSION", android.os.Build.VERSION.SDK);
            Field[] fields = Build.VERSION_CODES.class.getFields();
            String osName = fields[Build.VERSION.SDK_INT + 1].getName();

            device_information.put("DEVICE_OS", osName);
            device_information.put("APP_OPEN_COUNT", Count + "");
            device_information.put("Last_opended", getCurrentTime());
            device_information.put("First_opended", First_opended);


            if (user == null) {
                device_information.put("uid", "Not LogIn");

            } else {
                device_information.put("uid", user.getUid());
                device_information.put("email", user.getEmail());
                device_information.put("phone", user.getPhoneNumber());
                device_information.put("DisplayName", user.getDisplayName());

            }

            Config.APP_INSTALL_DEVICE_INFORMATION.child(unique_id).setValue(device_information);
//            Toast.makeText(this, "Unique id" + unique_id, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Config.APP_INSTALL_DEVICE_INFORMATION.child("Error Occoured").setValue("" + getCurrentTime());
        }

 */   }


}
