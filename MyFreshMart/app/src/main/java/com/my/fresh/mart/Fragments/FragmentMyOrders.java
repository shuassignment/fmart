package com.my.fresh.mart.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.my.fresh.mart.Config;
import com.my.fresh.mart.Model.Orders;
import com.my.fresh.mart.R;

import static com.my.fresh.mart.ParidizActivity.FIREBASE_UI_SIGN_IN;

/**
 * Created by intel on 03-Apr-18.
 */

public class FragmentMyOrders extends Fragment {


    RecyclerView rvOrders;
    DatabaseReference dbRefOrders;
    FirebaseUser user;

    Query mQuery;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_orders, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            startActivityForResult(Config.FIREBASE_LOGIN(), FIREBASE_UI_SIGN_IN);


        } else {

            rvOrders = view.findViewById(R.id.rvOrders);
            rvOrders.setHasFixedSize(true);
            rvOrders.setLayoutManager(new GridLayoutManager(getActivity(), 1));

            dbRefOrders = FirebaseDatabase.getInstance().getReference().child("Paridiz").child("Orders");

            mQuery = dbRefOrders.orderByChild("uid").equalTo(user.getUid());


            mQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null) {
                        Toast.makeText(getActivity(), "No Orders found", Toast.LENGTH_LONG).show();
                        TextView textView=view.findViewById(R.id.noOrders);
                        textView.setVisibility(View.VISIBLE);
                        rvOrders.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });




            FirebaseRecyclerAdapter<Orders, MyOrdersViewHolder> firebaseRecyclerAdapterOrders = new FirebaseRecyclerAdapter<Orders, MyOrdersViewHolder>(
                    Orders.class,
                    R.layout.orders_row,
                    MyOrdersViewHolder.class,
                    mQuery
            ) {
                @Override
                protected void populateViewHolder(MyOrdersViewHolder viewHolder, Orders model, int position) {

                    final String key = getRef(position).getKey();
                /*    viewHolder.setName(model.getName());
                    viewHolder.setNumber(model.getMobileNo());
                    viewHolder.setOrderAmount(model.getTotal());
                    viewHolder.setImage(getActivity(), model.getImage());
                    viewHolder.setPrice(model.getPrice());
                    viewHolder.setProductTitle(model.getTitle());
                    viewHolder.setProductDescription(model.getDescription());
                    viewHolder.setQuantity(model.getQuantity());
                    viewHolder.setAddress(model.getAddress());
                    viewHolder.setDeliveryAmount(model.getDelivery());
*/
                    viewHolder.ibCancelOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dbRefOrders.child(key).removeValue();
                        }
                    });

                }
            };

            rvOrders.setAdapter(firebaseRecyclerAdapterOrders);
        }


    }

    public static class MyOrdersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        ImageButton ibCancelOrder;
        public MyOrdersViewHolder(View itemView) {
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
