package com.my.fresh.mart;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by intel on 19-Mar-18.
 */

public class Config {

    /**
     * Fragment Stack
     */
    public static final String MAIN_STACK="MainStack";

    /**
     * SHARED PREFERENCES
     */

    public static final String SHARED_PREFERENCE_TNC="TermsAndConditions";

    /**
     * FIREBASE DATABASE
     */
    public static final String FRUITS="Fruits";
    public static final String VEGETABLES ="Vegetables";
    public static final String DRY_FRUITS="Dry_Fruits";


    public static final DatabaseReference BASE_URL= FirebaseDatabase.getInstance().getReference();
    public static final DatabaseReference PARIDIZ= BASE_URL.child("Paridiz");
    public static final DatabaseReference CUSTOMERS= PARIDIZ.child("Customers");
    public static final DatabaseReference ADDRESS= PARIDIZ.child("Address");
    public static final DatabaseReference CART= PARIDIZ.child("Cart");
    public static final DatabaseReference ORDERS= PARIDIZ.child("Orders");
    public static final DatabaseReference TNC= PARIDIZ.child("TnC");


    //Products
    public static final DatabaseReference PRODUCTS= BASE_URL.child("Products");
    public static final DatabaseReference DB_FRUITS= PRODUCTS.child(FRUITS);
    public static final DatabaseReference DB_VEGETABLES= PRODUCTS.child(VEGETABLES);
    public static final DatabaseReference DB_DRY_FRUITS= PRODUCTS.child(DRY_FRUITS);


    //auth
    public static final FirebaseAuth AUTH=FirebaseAuth.getInstance();

    //user
    public  static final FirebaseUser FIREBASE_USER = AUTH.getCurrentUser();


    public static final DatabaseReference APP_INSTALL_DEVICE_INFORMATION= BASE_URL.child("INSTALLS").child("DEVICE_INFORMATION");
    public static final String NUMBER_OF_TIMES_APP_OPEN="APP_OPEN_COUNT";


//    public static final DatabaseReference ADD_TO_CART= ;


    // Activity Request code

    public static final int GALLARY_REQUEST = 2;

    public static final int PAYMENT_REQUEST_CODE=10;


    public static  final  int FIREBASE_UI_SIGN_IN=1;

    public static android.content.Intent FIREBASE_LOGIN() {
           return      AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                        new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build())
                        ).setIsSmartLockEnabled(false)
                        .build();

    }

    public static void FIREBASE_LOGOUT(final AppCompatActivity Activitys)
    {
        FirebaseAuth.getInstance().signOut();

        AuthUI.getInstance()
                .signOut(Activitys)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        Toast.makeText(Activitys, "Logout Successful", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    //HashMap Of Products

    public static HashMap<String,Object> ProductMap(String imageUrl,String ProductName,String Remark,boolean isProductAvailable,HashMap<String,String> MapWeightPrice,String Catagory) {
        HashMap<String, Object> product = new HashMap<>();
        product.put("Image", imageUrl);
        product.put("ProductName", ProductName);
        product.put("AvailableQuantity", MapWeightPrice);
        product.put("Weight",MapWeightPrice.keySet().iterator().next());
        product.put("Price",MapWeightPrice.get(MapWeightPrice.keySet().iterator().next()));
        product.put("Availability", isProductAvailable);
        product.put("Remark", Remark);
        product.put("Category", Catagory);
        Log.e("ProductMap",product.toString());
        return product;
    }

    public static HashMap<String,Object> CartMap(String ProductKey,String imageUrl,String ProductName,String OfferPrice,String MRP,String Weight,String Quantity,String category,String time) {

        HashMap<String, Object> product = new HashMap<>();
        product.put("ProductKey", ProductKey);
        product.put("Image", imageUrl);
        product.put("ProductName", ProductName);
        product.put("OfferPrice", OfferPrice);
        product.put("MRP", MRP);
        product.put("Weight", Weight);
        product.put("Quantity", Quantity);
        product.put("Category",category);
        product.put("AddedInCartAt",time);

        return product;
    }
    public static HashMap<String,String> PlaceOrderMap(String ProductKey, String imageUrl, String ProductName, String OfferPrice, String MRP, String Weight, String Quantity, String category, String AddedInCartAt, String CurrentTime, String uid, String payment, String orderId) {

        HashMap<String, String> product = new HashMap<>();
        product.put("ProductKey", ProductKey);
        product.put("Image", imageUrl);
        product.put("ProductName", ProductName);
        product.put("OfferPrice", OfferPrice);
        product.put("MRP", MRP);
        product.put("Weight", Weight);
        product.put("Quantity", Quantity);
        product.put("Category",category);
        product.put("AddedInCartAt",AddedInCartAt);
        product.put("PlacedOrderAt",CurrentTime);
        product.put("Uid",uid);
        product.put("Payment",payment);
        product.put("OrderId",orderId);
        return product;
    }


    public static String getCurrentTime() {
        try {

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            return dateFormat.format(cal.getTime());
        } catch (Exception e) {
            return "Error";
        }
    }
}
