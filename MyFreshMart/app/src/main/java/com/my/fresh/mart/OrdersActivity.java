package com.my.fresh.mart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.my.fresh.mart.Model.Orders;

import static com.my.fresh.mart.ParidizActivity.FIREBASE_UI_SIGN_IN;

public class OrdersActivity extends AppCompatActivity {

    Boolean owner = false;
    RecyclerView rvOrders;
    DatabaseReference dbRefOrders;
    FirebaseUser user;

    Query mQuery,mQuery2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user==null)
        {
            startActivityForResult(Config.FIREBASE_LOGIN(), FIREBASE_UI_SIGN_IN);
        }else {

            Bundle data = getIntent().getExtras();
            owner = data.getBoolean("owner");
            rvOrders = findViewById(R.id.rvOrders);
            rvOrders.setHasFixedSize(true);
            rvOrders.setLayoutManager(new GridLayoutManager(this, 1));

            //dbRefOrders = FirebaseDatabase.getInstance().getReference().child("Paridiz").child("Orders");
            dbRefOrders = Config.ORDERS;

            if (owner) {
                //todo show orders customers wise
                mQuery = dbRefOrders.orderByKey();


                FirebaseRecyclerAdapter<Orders, CustomerViewHolder> firebaseRecyclerAdapterOrders = new FirebaseRecyclerAdapter<Orders, CustomerViewHolder>(
                        Orders.class,
                        R.layout.single_customer,
                        CustomerViewHolder.class,
                        mQuery
                ) {
                    @Override
                    protected void populateViewHolder(final CustomerViewHolder viewHolder, Orders model, int position) {

                        final String key = getRef(position).getKey();
                        final String userId=key;
                        Config.ADDRESS.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String CustName[] = {""};

                                try {
                                    CustName[0] = dataSnapshot.child("Name").getValue().toString();
                                    viewHolder.setCustomerName(CustName[0]);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                final String MobileNo[]={""};
                                try{
                                    MobileNo[0] = dataSnapshot.child("MobileNo").getValue().toString();
                                    viewHolder.setCustomerNumber(MobileNo[0]);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                String address;
                                try {
                                    address = dataSnapshot.child("Address").getValue().toString() + "\n" + dataSnapshot.child("City").getValue().toString() + "\t" + dataSnapshot.child("Pincode").getValue().toString() + "\n" + dataSnapshot.child("State").getValue().toString();
                                    viewHolder.setCustomerAddress(address);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                                final String[] deliveryTime = {""};
                                Config.PARIDIZ.child("ExpectedDeliveryTime").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        try {
                                            deliveryTime[0] = dataSnapshot.child("DeliveryTime").getValue().toString();
                                            viewHolder.setDeliveryTime(deliveryTime[0]);
                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        mQuery2 = dbRefOrders.child(key);

                                        FirebaseRecyclerAdapter<Orders, OrdersViewHolder> firebaseRecyclerAdapterOrders = new FirebaseRecyclerAdapter<Orders, OrdersViewHolder>(
                                                Orders.class,
                                                R.layout.orders_row,
                                                OrdersViewHolder.class,
                                                mQuery2
                                        ) {
                                            @Override
                                            protected void populateViewHolder(OrdersViewHolder ordersViewHolder, Orders model, int position) {

                                                final String key = getRef(position).getKey();
                                                ordersViewHolder.setName(CustName[0]);
                                                ordersViewHolder.setNumber(MobileNo[0]);
                                                int total=Integer.parseInt(model.getOfferPrice())*Integer.parseInt(model.getQuantity());
                                                ordersViewHolder.setOrderAmount(String.valueOf(total));
                                                ordersViewHolder.setImage(getApplicationContext(), model.getImage());
                                                ordersViewHolder.setPrice(model.getOfferPrice());
                                                ordersViewHolder.setProductTitle(model.getProductName());
                                                ordersViewHolder.setProductDescription(model.getWeight());
                                                ordersViewHolder.setQuantity(model.getQuantity());
                        //                    viewHolder.setAddress(model.getAddress());
                        //                    viewHolder.setDeliveryAmount(model.getDelivery());

                                                ordersViewHolder.ibCancelOrder.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {

                                                        AlertDialog.Builder DialogRemove=new AlertDialog.Builder(OrdersActivity.this);

                                                        DialogRemove
                                                                .setMessage("Are you sure you want to Cancel Order ?")
                                                                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                        dbRefOrders.child(userId).child(key).removeValue();

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

                                            }
                                        };

                                        rvOrders.setAdapter(firebaseRecyclerAdapterOrders);


                                    }
                                });


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        //viewHolder.setName(model.getProductName());
                        //viewHolder.setNumber(model.getMobileNo());
                        //viewHolder.setOrderAmount(model.getTotal());
                        /*viewHolder.setImage(getApplicationContext(), model.getImage());
                        viewHolder.setPrice(model.getOfferPrice());
                        viewHolder.setProductTitle(model.getProductName());
                        viewHolder.setProductDescription(model.getWeight());
                        *///viewHolder.setQuantity(model.getQuantity());
//                    viewHolder.setAddress(model.getAddress());
//                    viewHolder.setDeliveryAmount(model.getDelivery());
/*

                        viewHolder.ibCancelOrder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dbRefOrders.child(key).removeValue();
                            }
                        });
*/

                    }
                };

                rvOrders.setAdapter(firebaseRecyclerAdapterOrders);



            } else {
                mQuery = dbRefOrders.child(user.getUid());//.orderByChild("uid").equalTo(user.getUid());
            }

            Log.e("Order",mQuery.toString());
            mQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null) {
                        Toast.makeText(OrdersActivity.this, "No Orders found", Toast.LENGTH_LONG).show();
                        TextView textView=findViewById(R.id.noOrders);
                        textView.setVisibility(View.VISIBLE);
                        rvOrders.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            if(!owner) {
                FirebaseRecyclerAdapter<Orders, OrdersViewHolder> firebaseRecyclerAdapterOrders = new FirebaseRecyclerAdapter<Orders, OrdersViewHolder>(
                        Orders.class,
                        R.layout.orders_row,
                        OrdersViewHolder.class,
                        mQuery
                ) {
                    @Override
                    protected void populateViewHolder(OrdersViewHolder viewHolder, Orders model, int position) {

                        final String key = getRef(position).getKey();
                        //viewHolder.setName(model.getProductName());
                        //viewHolder.setNumber(model.getMobileNo());
                        //viewHolder.setOrderAmount(model.getTotal());
                        viewHolder.setImage(getApplicationContext(), model.getImage());
                        viewHolder.setPrice(model.getOfferPrice());
                        viewHolder.setProductTitle(model.getProductName());
                        viewHolder.setProductDescription(model.getWeight());
                        viewHolder.setQuantity(model.getQuantity());
                        int total=Integer.parseInt(model.getOfferPrice())*Integer.parseInt(model.getQuantity());
                        viewHolder.setOrderAmount(total+"");
                        viewHolder.setName("");
                        viewHolder.setAddress("");
                        viewHolder.setNumber("");

//                    viewHolder.setAddress(model.getAddress());
//                    viewHolder.setDeliveryAmount(model.getDelivery());

                        viewHolder.ibCancelOrder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                AlertDialog.Builder DialogRemove=new AlertDialog.Builder(OrdersActivity.this);

                                DialogRemove
                                        .setMessage("Are you sure you want to Cancel Order ?")
                                        .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dbRefOrders.child(user.getUid()).child(key).removeValue();

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

                    }
                };

                rvOrders.setAdapter(firebaseRecyclerAdapterOrders);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();


    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {

        View mView;

        ImageButton ibCancelOrder;
        public CustomerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
           // ibCancelOrder=mView.findViewById(R.id.ibCancelOrder);
        }

        public void setCustomerName(String name) {
            TextView CustomerName = mView.findViewById(R.id.tvCustomerName);

            CustomerName.setText(name);
        }

        public void setCustomerNumber(String number) {
            TextView CustomerNumber = mView.findViewById(R.id.tvCustomerNumber);

            CustomerNumber.setText(number);
        }

        public void setCustomerAddress(String address) {
            TextView Address = mView.findViewById(R.id.tvCustomerAddress);

            Address.setText(address);
        }
        public void setDeliveryTime(String deliveryTime) {
            TextView delivery = mView.findViewById(R.id.tvDeliveryTime);

            delivery.setText(deliveryTime);
        }
    }
public static class OrdersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        ImageButton ibCancelOrder;
        public OrdersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            ibCancelOrder=mView.findViewById(R.id.ibCancelOrder);
        }

        public void setName(String name) {
            TextView CustomerName = mView.findViewById(R.id.tvOrdersCustomerName);

            CustomerName.setText(name);
        }

        public void setNumber(String number) {
            TextView CustomerNumber = mView.findViewById(R.id.tvOrdersCustomerNumber);

            CustomerNumber.setText(number);
        }

        public void setOrderAmount(String amount) {
            TextView OrderAmount = mView.findViewById(R.id.tvOrdersAmount);

            OrderAmount.setText("₹ "+amount);
        }
        public void setImage(Context ctx, String image) {
            ImageView imageView = mView.findViewById(R.id.imgOrder);
//            Picasso.with(ctx).load(image).placeholder(R.drawable.placehoder_image).into(imageView);
        //todo check orders images
            Glide.with(ctx)
                    .load(image)
                    .placeholder(R.drawable.placehoder_image)
                    .into(imageView);

        }

        public void setProductTitle(String title) {
            TextView ProductTitle = mView.findViewById(R.id.tvOrdersProductTitle);

            ProductTitle.setText(title);
        }
        public void setProductDescription(String description) {
            TextView ProductDescription = mView.findViewById(R.id.tvOrdersProductDescription);

            ProductDescription.setText(description);
        }
        public void setQuantity(String quantity) {
            TextView Quantity = mView.findViewById(R.id.tvOrdersQuantity);

            Quantity.setText(quantity);
        }
        public void setPrice(String price) {
            TextView Price = mView.findViewById(R.id.tvOrdersItemPrice);

            Price.setText(price);
        }
        public void setAddress(String address) {
            TextView Address = mView.findViewById(R.id.tvOrdersCustomerAddress);

            Address.setText(address);
        }
        public void setDeliveryAmount(String deliveryAmount) {
            TextView Delivery = mView.findViewById(R.id.tvOrdersDeliveryAmount);

            Delivery.setText(" + "+deliveryAmount);
        }

    }
}
