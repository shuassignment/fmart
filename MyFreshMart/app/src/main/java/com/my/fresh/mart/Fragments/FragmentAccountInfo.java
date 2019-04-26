package com.my.fresh.mart.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.fresh.mart.R;

import static com.my.fresh.mart.Config.ADDRESS;
import static com.my.fresh.mart.Config.FIREBASE_USER;

/**
 * Created by intel on 09-Dec-17.
 */

public class FragmentAccountInfo extends Fragment {
    TextView tvName,tvAddress,tvMobileNo,tvPincode,tvCity,tvState;

    DatabaseReference dbAddress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView=inflater.inflate(R.layout.fragment_account_info,container,false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        dbAddress = FirebaseDatabase.getInstance().getReference().child("Paridiz").child("Address").child(user.getUid());

        tvName=view.findViewById(R.id.tvAccountInfoAddressViewName);
        tvAddress=view.findViewById(R.id.tvAccountInfoAddressViewAddress);
        tvMobileNo=view.findViewById(R.id.tvAccountInfoAddressViewMobileNo);
        tvPincode=view.findViewById(R.id.tvAccountInfoAddressViewPincode);
        tvCity=view.findViewById(R.id.tvAccountInfoAddressViewCity);
        tvState=view.findViewById(R.id.tvAccountInfoAddressViewState);

        try {

            ADDRESS.child(FIREBASE_USER.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    try {
                        String name, city, address, mobileNo, pincode, state;
                        name = dataSnapshot.child("Name").getValue().toString();
                        city = dataSnapshot.child("City").getValue().toString();
                        address = dataSnapshot.child("Address").getValue().toString();
                        pincode = dataSnapshot.child("Pincode").getValue().toString();
                        mobileNo = dataSnapshot.child("MobileNo").getValue().toString();
                        state = dataSnapshot.child("State").getValue().toString();

                        tvName.setText(name);
                        tvAddress.setText(address);
                        tvCity.setText(city);
//                            tvPincode.setText(pincode);
                        tvMobileNo.setText("Mobile Number : "+mobileNo);
//                            tvState.setText(state);

                        tvPincode.setVisibility(View.GONE);
                        tvState.setVisibility(View.GONE);
                        tvCity.append(", "+state);
                        tvCity.append(", "+pincode);

//                        addressCardView.setVisibility(View.VISIBLE);
                    }catch (Exception e)
                    {
                        Toast.makeText(getActivity(), "No Account Information found", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e)
        {
            Log.d("Error is :  ",""+e);
            Toast.makeText(getActivity(), "Unable to load data", Toast.LENGTH_SHORT).show();
        }


    }
}
