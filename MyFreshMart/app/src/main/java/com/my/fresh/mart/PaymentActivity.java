package com.my.fresh.mart;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PaymentActivity extends AppCompatActivity {

    Button btnPayBill;

    String payeeAddress = "shubhamsraut19@okicici";
    String payeeName = "Shubham Raut";
    String transactionNote = "Test for Deeplinking";
    String amount = "1";
    String currencyUnit = "INR";

    FirebaseUser user;

    TextView tvShowAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        tvShowAmount=findViewById(R.id.showAmount);

        user = FirebaseAuth.getInstance().getCurrentUser();

//        int b = 0, totalDelivery = 0;

        DatabaseReference dbrefs = FirebaseDatabase.getInstance().getReference();
        dbrefs.child("Paridiz").child("Cart").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int b = 0, totalDelivery = 0;

                for (DataSnapshot suggestiondataSnapshot : dataSnapshot.getChildren()) {

                    String mainCategory = suggestiondataSnapshot.child("category").getValue(String.class);
                    String PRICE = suggestiondataSnapshot.child("price").getValue(String.class);
                    String QUANTITY = suggestiondataSnapshot.child("quantity").getValue(String.class);
                    String delivery = suggestiondataSnapshot.child("delivery").getValue(String.class);


                    int mul = Integer.parseInt(PRICE) * Integer.parseInt(QUANTITY);


                    int xyz = Integer.parseInt(delivery);
                    totalDelivery = totalDelivery + xyz;
                    b = mul + b;


                }

                int t = b + totalDelivery;

                amount=String.valueOf(t);
                tvShowAmount.append(" "+amount);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        btnPayBill = findViewById(R.id.btnPayBill);

        btnPayBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = Uri.parse("upi://pay?pa=" + payeeAddress + "&pn=" + payeeName + "&tn=" + transactionNote +
                        "&am=" + amount + "&cu=" + currencyUnit);
                Log.d("onClick", "onClick: uri: " + uri);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivityForResult(intent, 1);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        String TAG = "";
        Log.d(TAG, "onActivityResult: requestCode: " + requestCode);
        Log.d(TAG, "onActivityResult: resultCode: " + resultCode);
        //txnId=UPI20b6226edaef4c139ed7cc38710095a3&responseCode=00&ApprovalRefNo=null&Status=SUCCESS&txnRef=undefined
        //txnId=UPI608f070ee644467aa78d1ccf5c9ce39b&responseCode=ZM&ApprovalRefNo=null&Status=FAILURE&txnRef=undefined

        if (data != null) {
            Log.d(TAG, "onActivityResult: data: " + data.getStringExtra("response"));
            String res = data.getStringExtra("response");
            String search = "SUCCESS";
            if (res.toLowerCase().contains(search.toLowerCase())) {
                Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("pos", "Payment Successful");
                setResult(Activity.RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("pos", "Payment Failed");
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }

    }
}
