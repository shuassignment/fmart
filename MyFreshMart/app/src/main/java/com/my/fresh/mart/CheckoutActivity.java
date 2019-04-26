package com.my.fresh.mart;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

import static com.my.fresh.mart.Config.PAYMENT_REQUEST_CODE;
import static com.my.fresh.mart.Config.getCurrentTime;
import static com.my.fresh.mart.ParidizActivity.FIREBASE_UI_SIGN_IN;

public class CheckoutActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    DatabaseReference dbAddress;
    String name, city, address, mobileNo, pincode, state, CustomerKey;
    FirebaseUser user;

    CardView addressCardView;
    TextView tvOrderTotal, tvDeliveryCharg, tvTotalValue;
    String OrderTotal;
    TextView tvName, tvAddress, tvMobileNo, tvPincode, tvCity, tvState, tvCODEligible;

    ImageButton ibAddNewAddress, ibEditAddress;
    Button btnPlaceOrder;

    CheckBox cbPayUsingBhimUpi;
    boolean payNow = false;
    ImageView imageViewDatePicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        imageViewDatePicker=findViewById(R.id.ivDatePicker);

        imageViewDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*final Dialog setDate = new Dialog(CheckoutActivity.this);
                setDate.requestWindowFeature(Window.FEATURE_NO_TITLE);
                setDate.setContentView(R.layout.dialog_setdate);
                setDate.show();
                DatePicker date = (DatePicker) setDate.findViewById(R.id.datePicker);
                date.setMinDate(System.currentTimeMillis() - 1000);
                Calendar calender = Calendar.getInstance();

                DatePicker.OnDateChangedListener onDateChangedListener = new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int years, int monthOfYear, int dayOfMonth) {
                        day = dayOfMonth;
                        year = years;
                        month = monthOfYear;
                        setDate.dismiss();
                        txt_arrival.setText("Arrival Date on: " + day + "." + (month + 1) + "." + year);
                        tv_departure.setText("Departure Date on: " + (day + 1) + "." + (month + 1) + "." + year + "");
                    }
                };

                day = calender.get(Calendar.DAY_OF_MONTH);
                month = calender.get(Calendar.MONTH);
                year = calender.get(Calendar.YEAR);

                date.init(year, month, day, onDateChangedListener);
*/
//                Calendar mcurrentDate = Calendar.getInstance();
//                int mYear = mcurrentDate.get(Calendar.YEAR);
//                int mMonth = mcurrentDate.get(Calendar.MONTH);
//                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int min= mcurrentTime.get(Calendar.MINUTE);

                final TextView textView=findViewById(R.id.tvDeliveryTime);
                DialogDeliveryTimings(textView);


/*
                TimePickerDialog Tp = new TimePickerDialog(CheckoutActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        textView.setText(hourOfDay+ ":" +minute);
                        //todo
                        DialogDeliveryTimings(textView);
//                        Config.PARIDIZ.child("ExpectedDeliveryTime").child(user.getUid()).child("DeliveryTime").setValue(hourOfDay+" : "+minute);
                    }
                },hour,min,false);
                Tp.show();
*/

            }
        });


        cbPayUsingBhimUpi = findViewById(R.id.payUsingBhimUpi);
        cbPayUsingBhimUpi.setVisibility(View.GONE);
        tvCODEligible = findViewById(R.id.tvEligibleForCOD);
        tvCODEligible.setVisibility(View.GONE);
        ibAddNewAddress = findViewById(R.id.ibCheckoutNewAddress);
        ibEditAddress = findViewById(R.id.ibCheckoutEditAddress);

        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        ibEditAddress.setVisibility(View.GONE);
        ibAddNewAddress.setVisibility(View.GONE);
        user = FirebaseAuth.getInstance().getCurrentUser();


        addressCardView = findViewById(R.id.addressView);
        addressCardView.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();

        tvOrderTotal = findViewById(R.id.tvCheckoutOrderTotalAmount);
        tvDeliveryCharg = findViewById(R.id.tvCheckoutOrderDelivery);
        tvTotalValue = findViewById(R.id.tvCheckoutOrderTotalValue);

        tvName = findViewById(R.id.tvCheckoutAddressViewName);
        tvAddress = findViewById(R.id.tvCheckoutAddressViewAddress);
        tvMobileNo = findViewById(R.id.tvCheckoutAddressViewMobileNo);
        tvPincode = findViewById(R.id.tvCheckoutAddressViewPincode);
        tvCity = findViewById(R.id.tvCheckoutAddressViewCity);
        tvState = findViewById(R.id.tvCheckoutAddressViewState);


        try {


            //DatabaseReference dbrefs = FirebaseDatabase.getInstance().getReference();

            Config.CART.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int orderTotal = 0, totalDelivery = 0;//todo fetch delivery charge

                    for (DataSnapshot suggestiondataSnapshot : dataSnapshot.getChildren()) {

//                        String mainCategory = suggestiondataSnapshot.child("Category").getValue(String.class);
                        try {
                            String productPrice = suggestiondataSnapshot.child("OfferPrice").getValue(String.class);
                            String productQuantity = suggestiondataSnapshot.child("Quantity").getValue(String.class);

                            int mul = Integer.parseInt(productPrice) * Integer.parseInt(productQuantity);

                            orderTotal = mul + orderTotal;
                        } catch (NumberFormatException e) {
                            Log.e("Exception", "" + e);
                        }
                    }

                    tvOrderTotal.setText("₹ " + orderTotal);
                    tvDeliveryCharg.setText("₹ " + getDeliveryCharge(orderTotal));
                    int t = orderTotal + totalDelivery;
                    tvTotalValue.setText("₹" + t);
//                    tvTotalAmount.setText("" + b);

//                    total = b;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } catch (Exception e) {
            tvOrderTotal.setText("" + e);
            Log.d("err", "" + e);
        }

        ////////////////////////////////////////////


        Bundle data = getIntent().getExtras();
        OrderTotal = data.getString("OrderTotal");

        dbAddress = Config.PARIDIZ.child("Address").child(user.getUid());

        if (user == null) {
            startActivityForResult(Config.FIREBASE_LOGIN(), FIREBASE_UI_SIGN_IN);

        } else {

            try {

                dbAddress.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        try {
                            CustomerKey = dataSnapshot.getKey();
                            Log.d("CustomerKey", CustomerKey);
                            name = dataSnapshot.child("Name").getValue().toString().trim();
                            city = dataSnapshot.child("City").getValue().toString().trim();
                            address = dataSnapshot.child("Address").getValue().toString().trim();
                            pincode = dataSnapshot.child("Pincode").getValue().toString().trim();
                            mobileNo = dataSnapshot.child("MobileNo").getValue().toString().trim();
                            state = dataSnapshot.child("State").getValue().toString().trim();


                            btnPlaceOrder.setVisibility(View.VISIBLE);

                            tvCODEligible.setVisibility(View.VISIBLE);
                            tvCODEligible.setText("Congrats ... You are eligible for cash on delivery!!!");
                            tvCODEligible.setTextColor(Color.parseColor("#00796b"));

                            tvName.setText(name);
                            tvAddress.setText("Address : " + address);
                            tvCity.setText(city);
//                            tvPincode.setText(pincode);
                            tvMobileNo.setText("Mobile No : " + mobileNo);
//                            tvState.setText(state);

                            tvPincode.setVisibility(View.GONE);
                            tvState.setVisibility(View.GONE);
                            tvCity.append(", " + state);
                            tvCity.append(", " + pincode);

                            addressCardView.setVisibility(View.VISIBLE);
                            ibEditAddress.setVisibility(View.VISIBLE);
                            ibAddNewAddress.setVisibility(View.GONE);
                        } catch (Exception e) {
                            ibAddNewAddress.setVisibility(View.VISIBLE);
                            ibEditAddress.setVisibility(View.GONE);

//                            Snackbar.make()
                            tvCODEligible.setVisibility(View.VISIBLE);
                            tvCODEligible.setText("Please Add a new Address");

                            Toast.makeText(CheckoutActivity.this, "Please Add a new Address", Toast.LENGTH_SHORT).show();
                            btnPlaceOrder.setVisibility(View.GONE);

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } catch (Exception e) {
                Log.d("Error is :  ", "" + e);
                Toast.makeText(this, "Unable to load data", Toast.LENGTH_SHORT).show();
            }


//            btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    Toast.makeText(CheckoutActivity.this, "CheckoutActivity SetOnClickListner", Toast.LENGTH_SHORT).show();
//
//                    String ToatalOrderAmount;
//
//                    DatabaseReference dbRefOrders=FirebaseDatabase.getInstance().getReference().child("Paridiz").child("Orders").child(user.getUid());
//
//                    final HashMap<String,String> orders=new HashMap<>();
//                    orders.put("name",name);
//                    orders.put("mobileNo",mobileNo);
//                    orders.put("address",address+", "+city+", "+state+", "+pincode);
//                    orders.put("uid",user.getUid());
//
//                    try {
//
//
//                        DatabaseReference dbrefs = FirebaseDatabase.getInstance().getReference();
//                        dbrefs.child("Paridiz").child("Cart").child(user.getUid()).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                int b = 0,totalDelivery=0;
//
//                                for (DataSnapshot suggestiondataSnapshot : dataSnapshot.getChildren()) {
//
//                                    String mainCategory=suggestiondataSnapshot.child("category").getValue(String.class);
//                                    String subCategoryOne = suggestiondataSnapshot.child("price").getValue(String.class);
//                                    String subCategoryTwo = suggestiondataSnapshot.child("quantity").getValue(String.class);
//                                    String delivery=suggestiondataSnapshot.child("delivery").getValue(String.class);
//
//
//
//
//                                    int mul = Integer.parseInt(subCategoryOne) * Integer.parseInt(subCategoryTwo);
////                        Toast.makeText(MyCartActivity.this, ""+subCategoryOne, Toast.LENGTH_SHORT).show();
//                                    int a = Integer.parseInt(subCategoryOne);
//                                    int xyz=Integer.parseInt(delivery);
//                                    totalDelivery=totalDelivery+xyz;
//                                    b = mul + b;
//
//                                    String temp = subCategoryOne;
//                                    if (subCategoryOne != null) {
//
//                                    }
//
//                                    int ttt=mul+xyz;
//                                    DatabaseReference dbRefOrdersItems=FirebaseDatabase.getInstance().getReference().child("Paridiz").child("Orders").push();
//
//                                    String description=suggestiondataSnapshot.child("description").getValue(String.class);
//                                    String image=suggestiondataSnapshot.child("image").getValue(String.class);
//                                    String title=suggestiondataSnapshot.child("title").getValue(String.class);
//
//                                    HashMap<String,String> orderedItems=new HashMap<>();
//                                    orderedItems.put("description",description);
//                                    orderedItems.put("image",image);
//                                    orderedItems.put("title",title);
//                                    orderedItems.put("price",subCategoryOne);
//                                    orderedItems.put("quantity",subCategoryTwo);
//                                    orderedItems.put("delivery",delivery);
//
//                                    orderedItems.put("total",""+ttt);
//                                    orderedItems.put("name",name);
//                                    orderedItems.put("mobileNo",mobileNo);
//                                    orderedItems.put("address",address+", "+city+", "+state+", "+pincode);
//                                    orderedItems.put("uid",user.getUid());
//                                    orderedItems.put("payment","COD");
//
//                                    dbRefOrdersItems.setValue(orderedItems);
//
//                                }
////                    tvOrderTotal.setText("₹ "+b);
////                    tvDeliveryCharg.setText("₹ "+totalDelivery);
//                                int t=b+totalDelivery;
//
//                                orders.put("OrderAmount",""+t);
//                                //                    tvTotalAmount.setText("" + b);
////                    total = b;
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//
//                        dbrefs.child("Paridiz").child("Cart").child(user.getUid()).removeValue();
//                    } catch (Exception e) {
//                        tvOrderTotal.setText("" + e);
//                        Log.d("err", "" + e);
//                    }
////        dbRefOrders.setValue(orders);
//
//
//                    //        orders.put("city",city);
////        orders.put("state",state);
////        orders.put("pincode",pincode);
//                    AlertDialog.Builder builder=new AlertDialog.Builder(CheckoutActivity.this);
//                    builder.setTitle("Thank you for your order !!!");
//                    builder.setMessage("Order Successful");
//                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            Intent intent=new Intent(CheckoutActivity.this,OrdersActivity.class);
//                            intent.putExtra("owner",false);
//                            startActivity(intent);
//                            finish();
//                        }
//                    });
//                    builder.create().show();
//
//
////                    CheckoutActivity.this.finish();
//
//                }
//            });


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payNow = cbPayUsingBhimUpi.isChecked();

                if (payNow) {
                    Intent intent = new Intent(CheckoutActivity.this, PaymentActivity.class);
//                    startActivity(intent);
                    intent.putExtra("CustName", name);
//                    intent.putExtra("Amount",);

                    //todo BHIM UPI
                    //startActivityForResult(intent, 10);
                } else {

                    String ToatalOrderAmount;

                    DatabaseReference dbRefOrders = Config.PARIDIZ.child("Orders").child(user.getUid());

                    final HashMap<String, String> orders = new HashMap<>();
                    orders.put("name", name);
                    orders.put("mobileNo", mobileNo);
                    orders.put("address", address + ", " + city + ", " + state + ", " + pincode);
                    orders.put("uid", user.getUid());

                    try {

                        DatabaseReference dbrefs = FirebaseDatabase.getInstance().getReference();
                        dbrefs.child("Paridiz").child("Cart").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int b = 0, totalDelivery = 0;
                                //todo
                                for (DataSnapshot suggestiondataSnapshot : dataSnapshot.getChildren()) {

                                    String OfferPrice = suggestiondataSnapshot.child("OfferPrice").getValue(String.class);
                                    String Quantity = suggestiondataSnapshot.child("Quantity").getValue(String.class);

                                    int mul = Integer.parseInt(OfferPrice) * Integer.parseInt(Quantity);

                                    int a = Integer.parseInt(OfferPrice);

                                    b = mul + b;

                                    int ttt = mul;
//                                    DatabaseReference dbRefOrdersItems = FirebaseDatabase.getInstance().getReference().child("Paridiz").child("Orders").push();
                                    DatabaseReference dbRefOrdersItems = Config.PARIDIZ.child("Orders").child(user.getUid()).push();

                                    String description = suggestiondataSnapshot.child("description").getValue(String.class);
                                    String ImageURL = suggestiondataSnapshot.child("Image").getValue(String.class);
                                    String ProductName = suggestiondataSnapshot.child("ProductName").getValue(String.class);
                                    String ProductKey = suggestiondataSnapshot.child("ProductKey").getValue(String.class);
                                    ;
                                    String MRP = suggestiondataSnapshot.child("MRP").getValue(String.class);
                                    ;
                                    String Weight = suggestiondataSnapshot.child("Weight").getValue(String.class);
                                    ;
//                                    String Quantity=suggestiondataSnapshot.child("Quantity").getValue(String.class);;
                                    String Category = suggestiondataSnapshot.child("AddedInCartAt").getValue(String.class);
                                    ;
                                    String AddedInCartAt = suggestiondataSnapshot.child("AddedInCartAt").getValue(String.class);
                                    HashMap<String, String> orderedItems = new HashMap<>();
                                    orderedItems.put("description", description);

                                    orderedItems.put("total", "" + ttt);
                                    orderedItems.put("name", name);
                                    orderedItems.put("mobileNo", mobileNo);
                                    orderedItems.put("address", address + ", " + city + ", " + state + ", " + pincode);
//                                  orderedItems.put("uid", user.getUid());
                                    orderedItems.put("payment", "COD");

                                    //todo implement order ID logic
                                    final String orderId = ProductName.substring(0, 3) + Weight + getCurrentTime();
                                    HashMap hashMap = Config.PlaceOrderMap(ProductKey, ImageURL, ProductName, OfferPrice, MRP, Weight, Quantity, Category, AddedInCartAt, Config.getCurrentTime(), user.getUid(), "COD", orderId);

                                    dbRefOrdersItems.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {


                                            final Dialog dialog = new Dialog(CheckoutActivity.this);
                                            //  dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            dialog.setCancelable(false);
                                            dialog.setContentView(R.layout.order_successful);


                                            TextView text = dialog.findViewById(R.id.tvOrderId);
                                            text.setText(orderId);

                                            Button dialogButton = (Button) dialog.findViewById(R.id.btnContinueShopping);
                                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                    clearBackStack();

                                                    Intent intent = new Intent(CheckoutActivity.this, CustomerMainActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);

                                                }
                                            });

                                            dialog.show();


                                        }
                                    });

                                }

                                int t = b + totalDelivery;

                                orders.put("OrderAmount", "" + t);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        dbrefs.child("Paridiz").child("Cart").child(user.getUid()).removeValue();
                    } catch (Exception e) {
                        tvOrderTotal.setText("" + e);
                        Log.d("err", "" + e);
                    }
/*

                    AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
                    builder.setTitle("Thank you for your order !!!");
                    builder.setMessage("Order Successful");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            final InterstitialAd mInterstitialAd;

                            mInterstitialAd = new InterstitialAd(CheckoutActivity.this);
                            AdRequest adRequest = new AdRequest.Builder()
                                    .build();
                            // set the ad unit ID
                            mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

                            // Load ads into Interstitial Ads
                            mInterstitialAd.loadAd(adRequest);

                            mInterstitialAd.setAdListener(new AdListener() {
                                public void onAdLoaded() {
                                    if (mInterstitialAd.isLoaded()) {
                                        mInterstitialAd.show();
                                    }
                                }
                            });


                            Intent intent = new Intent(CheckoutActivity.this, OrdersActivity.class);
                            intent.putExtra("owner", false);
                            startActivity(intent);
                            finish();


                        }
                    });
                    builder.create().show();
*/

                }
            }
        });


    }

        /*
            All the stack fregments are removed
        */

    private void clearBackStack() {
        FragmentManager fm = CheckoutActivity.this.getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

    }

    public void addressActivity(View view) {
        Intent intent = new Intent(CheckoutActivity.this, AddressActivity.class);
        startActivity(intent);
    }

    public void placeorder(View view) {

//        String ToatalOrderAmount;
//
//        DatabaseReference dbRefOrders=FirebaseDatabase.getInstance().getReference().child("Paridiz").child("Orders").child(user.getUid());
//
//        final HashMap<String,String> orders=new HashMap<>();
//        orders.put("name",name);
//        orders.put("mobileNo",mobileNo);
//        orders.put("address",address+", "+city+", "+state+", "+pincode);
//        orders.put("uid",user.getUid());
//
//        try {
//
//
//            DatabaseReference dbrefs = FirebaseDatabase.getInstance().getReference();
//            dbrefs.child("Paridiz").child("Cart").child(user.getUid()).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    int b = 0,totalDelivery=0;
//
//                    for (DataSnapshot suggestiondataSnapshot : dataSnapshot.getChildren()) {
//
//                        String mainCategory=suggestiondataSnapshot.child("category").getValue(String.class);
//                        String subCategoryOne = suggestiondataSnapshot.child("price").getValue(String.class);
//                        String subCategoryTwo = suggestiondataSnapshot.child("quantity").getValue(String.class);
//                        String delivery=suggestiondataSnapshot.child("delivery").getValue(String.class);
//
//
//
//
//                        int mul = Integer.parseInt(subCategoryOne) * Integer.parseInt(subCategoryTwo);
////                        Toast.makeText(MyCartActivity.this, ""+subCategoryOne, Toast.LENGTH_SHORT).show();
//                        int a = Integer.parseInt(subCategoryOne);
//                        int xyz=Integer.parseInt(delivery);
//                        totalDelivery=totalDelivery+xyz;
//                        b = mul + b;
//
//                        String temp = subCategoryOne;
//                        if (subCategoryOne != null) {
//
//                        }
//
//                        int ttt=mul+xyz;
//                        DatabaseReference dbRefOrdersItems=FirebaseDatabase.getInstance().getReference().child("Paridiz").child("Orders").push();
//
//                        String description=suggestiondataSnapshot.child("description").getValue(String.class);
//                        String image=suggestiondataSnapshot.child("image").getValue(String.class);
//                        String title=suggestiondataSnapshot.child("title").getValue(String.class);
//
//                        HashMap<String,String> orderedItems=new HashMap<>();
//                        orderedItems.put("description",description);
//                        orderedItems.put("image",image);
//                        orderedItems.put("title",title);
//                        orderedItems.put("price",subCategoryOne);
//                        orderedItems.put("quantity",subCategoryTwo);
//                        orderedItems.put("delivery",delivery);
//
//                        orderedItems.put("total",""+ttt);
//                        orderedItems.put("name",name);
//                        orderedItems.put("mobileNo",mobileNo);
//                        orderedItems.put("address",address+", "+city+", "+state+", "+pincode);
//                        orderedItems.put("uid",user.getUid());
//                        orderedItems.put("payment","COD");
//
//                        dbRefOrdersItems.setValue(orderedItems);
//
//                    }
////                    tvOrderTotal.setText("₹ "+b);
////                    tvDeliveryCharg.setText("₹ "+totalDelivery);
//                    int t=b+totalDelivery;
//
//                    orders.put("OrderAmount",""+t);
//                                        //                    tvTotalAmount.setText("" + b);
////                    total = b;
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//            dbrefs.child("Paridiz").child("Cart").child(user.getUid()).removeValue();
//        } catch (Exception e) {
//            tvOrderTotal.setText("" + e);
//            Log.d("err", "" + e);
//        }
////        dbRefOrders.setValue(orders);
//
//
//        //        orders.put("city",city);
////        orders.put("state",state);
////        orders.put("pincode",pincode);
//        AlertDialog.Builder builder=new AlertDialog.Builder(this);
//        builder.setTitle("Thank you for your order !!!");
//        builder.setMessage("Order Successful");
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Intent intent=new Intent(CheckoutActivity.this,OrdersActivity.class);
//                intent.putExtra("owner",false);
//                startActivity(intent);
//                finish();
//            }
//        });
//        builder.create().show();
//

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PAYMENT_REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("pos");
                Toast.makeText(this, "" + result, Toast.LENGTH_SHORT).show();

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // some stuff that will happen if there's no result
            }
        }
    }

    private int getDeliveryCharge(int cartAmount) {
        SharedPreferences prefs = getSharedPreferences(Config.SHARED_PREFERENCE_TNC, MODE_PRIVATE);
        String delivery = prefs.getString("DeliveryCharges", null);
        Log.d("delivery1", delivery);

        //{1000=0, 100=10, 500=20, 999=10}

        HashMap<String, String> hashMap = new HashMap<>();
        delivery = delivery.replace("{", "");
        delivery = delivery.replace("}", "");

        for (String arr : delivery.split(", ")) {

            String deliveryDetails[] = arr.split("=");
            hashMap.put(deliveryDetails[0], deliveryDetails[1]);

        }
        Set<String> ss = hashMap.keySet();
        ArrayList<String> list = new ArrayList(ss);
        Log.d("delivery2", ss.toString());
        Log.d("delivery3", list.toString());

        Log.d("delivery4", hashMap.toString());

        if (cartAmount >= Integer.parseInt(list.get(0)) && cartAmount < Integer.parseInt(list.get(1))) {
            return (int) Integer.parseInt(hashMap.get(list.get(0)));
        } else if (cartAmount >= Integer.parseInt(list.get(1)) && cartAmount < Integer.parseInt(list.get(2))) {
            return (int) Integer.parseInt(hashMap.get(list.get(1)));
        } else if (cartAmount >= Integer.parseInt(list.get(2)) && cartAmount < Integer.parseInt(list.get(3))) {
            Log.d("deli", hashMap.get(list.get(2)));
            return (int) Integer.parseInt(hashMap.get(list.get(2)));
        } else if (cartAmount >= Integer.parseInt(list.get(3))) {
            return (int) Integer.parseInt(hashMap.get(list.get(3)));
        }

        return 10;
    }

    ArrayList<String> AvailableCategoryArraylist;

    private void DialogDeliveryTimings(final TextView textView) {


        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_available_quantities);

        TextView tvProductName=dialog.findViewById(R.id.tvProductName);
        TextView title=dialog.findViewById(R.id.title);
        title.setText("Select Delivery Timing");
        tvProductName.setVisibility(View.GONE);
//        tvProductName.setText(ProductName);

        AvailableCategoryArraylist = new ArrayList<>();
        AvailableCategoryArraylist.add("04:00 PM - 05:00 PM");
        AvailableCategoryArraylist.add("05:00 PM - 06:00 PM");
        AvailableCategoryArraylist.add("06:00 PM - 07:00 PM");
        AvailableCategoryArraylist.add("07:00 PM - 08:00 PM");
        AvailableCategoryArraylist.add("08:00 PM - 09:00 PM");

        ListView AvailableQuantities=dialog.findViewById(R.id.lvAvailableQuantities);
        final DeliveryTimingsAdapter deliveryTimingsAdapter= new DeliveryTimingsAdapter();
        AvailableQuantities.setAdapter(deliveryTimingsAdapter);
/*

        Config.PRODUCTS.child(category).child(ProductKey).child("AvailableQuantity").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                TextView tvQuantity=dialog.findViewById(R.id.tvQuantity);

                StringBuffer sb=new StringBuffer();
                sb.append(dataSnapshot.toString());
                tvQuantity.setText(sb.toString());
                Log.d("dialog1",dataSnapshot.toString());
                Map<String,String> map = (HashMap<String,String>) dataSnapshot.getValue();

                Iterator it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();

                    String price=pair.getValue().toString();
                    String s[]=price.split(",");

//                    AvailableCategoryArraylist.add("<b> " +pair.getKey() + "</b> - " +" MRP : <strike> " +s[0]+" </strike>  <b> "+s[1]+"</b>" +"");

//                    AvailableCategoryArraylist.add(new FragmentProductList.ProductPrice(s[0],s[1],pair.getKey().toString()));
                    System.out.println(pair.getKey() + " = " + pair.getValue());
                    it.remove(); // avoids a ConcurrentModificationException
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

*/

//        AvailableQuantities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//            }
//        });

        AvailableQuantities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();

                Log.i("selectedItem",selectedItem);

                String time=AvailableCategoryArraylist.get(i);
                textView.setText(time);
                Config.PARIDIZ.child("ExpectedDeliveryTime").child(user.getUid()).child("DeliveryTime").setValue(time);

//                viewHolder.arrIndex=i;
//                viewHolder.AvailableCategoryArraylistHolder=new ArrayList<>(AvailableCategoryArraylist);
//                viewHolder.setWeight(AvailableCategoryArraylist.get(i).getWeight());
//                viewHolder.setPrice(AvailableCategoryArraylist.get(i).getMRP()+"   "+AvailableCategoryArraylist.get(i).getOfferPrice());
//                viewHolder.setMRP(AvailableCategoryArraylist.get(i).getMRP());

                dialog.cancel();

            }
        });



      /*  Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String wt, mrp, off;
                EditText etWeight, etMRP, etOfferPrice;

                etWeight = dialog.findViewById(R.id.etWeight);
                etMRP = dialog.findViewById(R.id.etMRP);
                etOfferPrice = dialog.findViewById(R.id.etOfferPrice);

                wt = etWeight.getText().toString();
                mrp = etMRP.getText().toString();
                off = etOfferPrice.getText().toString();

                String MapValueWithMrpAndOffer = mrp + "," + off;
                MapWeightPrice.put(wt, MapValueWithMrpAndOffer);
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("Weight  :  MRP   :  Offer Price");

                Iterator it = MapWeightPrice.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
//                                System.out.println(pair.getKey() + " = " + pair.getValue());
                    String s = pair.getValue().toString();
                    String[] arr = s.split(",");
                    stringBuffer.append("\n " + pair.getKey() + "");
                    for (String s1 : arr) {
                        stringBuffer.append("\t " + s1 + "\t");
                    }

                }
                tvQuantity.setText(stringBuffer.toString());


                dialog.dismiss();
            }
        });

*/

        dialog.show();


    }
    public class DeliveryTimingsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return AvailableCategoryArraylist .size();
        }

        @Override
        public Object getItem(int i) {
            return AvailableCategoryArraylist .get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            LayoutInflater layoutInflater = getLayoutInflater();
            View view1 = layoutInflater.inflate(R.layout.categories_single_item, null);

            TextView txtSingleItem = view1.findViewById(R.id.tvListItem);
            txtSingleItem.setText(Html.fromHtml(AvailableCategoryArraylist .get(i).toString()));

            return view1;
        }
    }


}
