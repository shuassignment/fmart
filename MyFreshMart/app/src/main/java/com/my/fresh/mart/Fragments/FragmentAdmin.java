package com.my.fresh.mart.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.my.fresh.mart.Config;
import com.my.fresh.mart.R;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by intel on 24-Sep-18.
 */

public class FragmentAdmin extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final SharedPreferences.Editor editor = getActivity().getSharedPreferences(Config.SHARED_PREFERENCE_TNC, MODE_PRIVATE).edit();

        Config.TNC.child("MinCartValueToCheckout").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        final EditText etMinCartValue=view.findViewById(R.id.etMinCartValue);
        Button btnSaveMinCartValue=view.findViewById(R.id.btnSaveMinCartValue);
        btnSaveMinCartValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.PARIDIZ.child("TnC").child("MinCartValueToCheckout").setValue(etMinCartValue.getText().toString());
                Toast.makeText(getContext(), "Minimum Value Updated", Toast.LENGTH_SHORT).show();

                editor.putString("MinCartValue",etMinCartValue.getText().toString());
                editor.apply();

            }
        });

        final EditText aUpto,bUpto,cUpto,dUpto,aCharge,bCharge,cCharge,dCharge;

        aUpto=view.findViewById(R.id.aUpto);
        bUpto=view.findViewById(R.id.bUpto);
        cUpto=view.findViewById(R.id.cUpto);
        dUpto=view.findViewById(R.id.dUpto);
        aCharge=view.findViewById(R.id.aCharge);
        bCharge=view.findViewById(R.id.bCharge);
        cCharge=view.findViewById(R.id.cCharge);
        dCharge=view.findViewById(R.id.dCharge);
//
//        int aU=Integer.parseInt(aUpto.getText().toString());
//        int bU=Integer.parseInt(bUpto.getText().toString());
//        int cU=Integer.parseInt(aUpto.getText().toString());
//        int dU=Integer.parseInt(aUpto.getText().toString());
//
//        int aC=Integer.parseInt(aUpto.getText().toString());
//        int bc=Integer.parseInt(aUpto.getText().toString());
//        int cC=Integer.parseInt(aUpto.getText().toString());
//        int dC=Integer.parseInt(aUpto.getText().toString());

        final HashMap<String,String> hashMap=new HashMap<>();



        view.findViewById(R.id.btnSaveDeliveryCharges).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hashMap.put(aUpto.getText().toString(),aCharge.getText().toString());
                hashMap.put(bUpto.getText().toString(),bCharge.getText().toString());
                hashMap.put(cUpto.getText().toString(),cCharge.getText().toString());
                hashMap.put(dUpto.getText().toString(),dCharge.getText().toString());

                Config.PARIDIZ.child("TnC").child("DeliveryCharges").setValue(hashMap);
                editor.putString("DeliveryCharges",hashMap.toString());
                editor.apply();
            }
        });

        Button buttonSaveOffer=view.findViewById(R.id.btnSaveOffer);

        final EditText etOffer1=view.findViewById(R.id.etOffer1);
        final EditText etOffer2=view.findViewById(R.id.etOffer2);
        final EditText etOffer3=view.findViewById(R.id.etOffer3);


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
//                    etOffer1.setVisibility(View.GONE);
                }else {
                    etOffer1.setVisibility(View.VISIBLE);
                    etOffer1.setText(offer1);
                }
                if (offer2==null || offer2.equals("")){
//                    etOffer2.setVisibility(View.GONE);
                }else {
                    etOffer2.setVisibility(View.VISIBLE);
                    etOffer2.setText(offer2);
                }
                if (offer3==null || offer3.equals("")){
//                    etOffer3.setVisibility(View.GONE);
                }else {
                    etOffer3.setVisibility(View.VISIBLE);
                    etOffer3.setText(offer3);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        buttonSaveOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String offer1,offer2,offer3;

                offer1=etOffer1.getText().toString().trim();
                offer2=etOffer2.getText().toString().trim();
                offer3=etOffer3.getText().toString().trim();
                HashMap<String,String> hashMapOffers=new HashMap<>();
                hashMapOffers.put("Offer1",offer1);
                hashMapOffers.put("Offer2",offer2);
                hashMapOffers.put("Offer3",offer3);
                Config.PARIDIZ.child("Offers").setValue(hashMapOffers);

            }
        });

    }
}
