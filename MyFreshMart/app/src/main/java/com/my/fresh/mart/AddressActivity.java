package com.my.fresh.mart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.my.fresh.mart.ParidizActivity.FIREBASE_UI_SIGN_IN;

public class AddressActivity extends AppCompatActivity {

    EditText etName, etMobileNo, etPincode, etAddress, etCity, etState;
    RadioGroup rgAddressTye;
    Button btnSave;
    private FirebaseAuth mAuth;
    DatabaseReference dbAddress,dbCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        etName=findViewById(R.id.etAddressActivityFullName);
        etMobileNo=findViewById(R.id.etAddressActivityMobileNo);
        etState=findViewById(R.id.etAddressActivityState);
        etPincode=findViewById(R.id.etAddressActivityPincode);
        etCity=findViewById(R.id.etAddressActivityCity);
        etAddress=findViewById(R.id.etAddressActivityAddress);

        mAuth = FirebaseAuth.getInstance();

        btnSave=findViewById(R.id.btnSaveAddress);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        dbCustomer= FirebaseDatabase.getInstance().getReference().child("Paridiz").child("Customers").child(user.getUid());
        dbAddress = FirebaseDatabase.getInstance().getReference().child("Paridiz").child("Address").child(user.getUid());
        if (user != null) {

            dbAddress.addValueEventListener(new ValueEventListener() {
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

                        etName.setText(name);
                        etAddress.setText(address);
                        etCity.setText(city);
                        etPincode.setText(pincode);
                        etMobileNo.setText(mobileNo);
                        etState.setText(state);

//                        tvPincode.setVisibility(View.GONE);
//                        tvState.setVisibility(View.GONE);
//                        tvCity.append("  "+state);
//                        tvCity.append("  "+pincode);


                    }catch (Exception e)
                    {
                        Toast.makeText(AddressActivity.this, "Please Add a new Address", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uid = user.getUid();

                    dbAddress.child("uid").setValue(uid);
                    dbAddress.child("Name").setValue(etName.getText().toString().trim());
                    dbAddress.child("MobileNo").setValue(etMobileNo.getText().toString().trim());
                    dbAddress.child("Address").setValue(etAddress.getText().toString().trim());
                    dbAddress.child("City").setValue(etCity.getText().toString().trim());
                    dbAddress.child("State").setValue(etState.getText().toString().trim());
                    dbAddress.child("Pincode").setValue(etPincode.getText().toString().trim());

                    dbCustomer.child("Name").setValue(etName.getText().toString().trim());
                    dbCustomer.child("MobileNo").setValue(etMobileNo.getText().toString().trim());
                    dbCustomer.child("uid").setValue(uid);
                    finish();

                }
            });

        } else {
            startActivityForResult(Config.FIREBASE_LOGIN(), FIREBASE_UI_SIGN_IN);

        }
    }

}
