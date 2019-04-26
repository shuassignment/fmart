package com.my.fresh.mart;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static com.my.fresh.mart.Config.GALLARY_REQUEST;

public class MainActivity extends AppCompatActivity {

    String category, sc1, sc2, sc3, sc4, sc5, sc6 = " ";//sc:SubCategory
    String imagename, delivery;


    StorageReference mStorage;
    Button saveChanges, btn, btnWeight;
//    AutoCompleteTextView subCategory, subCategorytwo;
    ImageView img;
    ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    EditText etProductName, etDesc, etPrice, etDelivery, etRemark;

    CheckBox cbIsProductAvailable;
    Boolean editProductDetails;
    Bundle data;

    HashMap<String, String> MapWeightPrice,previousMapWeightPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find All Views here
        initializeUI();

        MapWeightPrice = new HashMap<>(); //stores hash map of product weight and respective price
        previousMapWeightPrice=new HashMap<>();
        try {

            data = getIntent().getExtras();
            category = data.getString("CategoryValue");
//            sc1 = data.getString("sc1");
//            sc2 = data.getString("sc2");
//            sc3 = data.getString("sc3");
//            sc4 = data.getString("sc4");
//            sc5 = data.getString("sc5");
//            sc6 = data.getString("sc6");

            editProductDetails = data.getBoolean("editProductDetails", false);


//            String subCategoryList[] = {sc1, sc2, sc3, sc4, sc5};

//            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, subCategoryList);
//            subCategory.setAdapter(adapter);

            try {
//                mAuth = FirebaseAuth.getInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (editProductDetails) {

                //todo fetch map from firebase and assign to MapWeightPrice
                Config.PRODUCTS.child(data.getString("category")).child(data.getString("productKey")).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("MainActivity.DataSnap",dataSnapshot.toString());
                        Log.d("MainActivity.avail",dataSnapshot.child("AvailableQuantity").toString());
                        previousMapWeightPrice = (HashMap<String,String>) dataSnapshot.child("AvailableQuantity").getValue();
                        Log.d("MapWeightPrice",previousMapWeightPrice.toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
//                MapWeightPrice
                Toast.makeText(this, "Category : " + data.getString("editProductCategory"), Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Category : " + data.getString("category"), Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "key : " + data.getString("editProductKey"), Toast.LENGTH_SHORT).show();
                btn.setVisibility(View.GONE);
                saveChanges.setVisibility(View.VISIBLE);
                etProductName.setText(data.getString("editProductName"));

                etPrice.setText(data.getString("editProductPrice"));
                etDelivery.setText(data.getString("editProductDelivery"));
//                subCategory.setText(data.getString("editProductSubCategoryOne"));
//                subCategorytwo.setText(data.getString("editProductSubCategoryTwo"));

                saveChanges.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*
                        db.setValue(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, "Product added Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, "Error Occoured Please try after some time", Toast.LENGTH_SHORT).show();
                            }
                        });*/

                    }
                });

            } else {

                mStorage = FirebaseStorage.getInstance().getReference().child("Photos");
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Products").child(category);
                progressDialog = new ProgressDialog(this);



                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        imagename = etProductName.getText().toString() + "_" + random();
                        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        galleryIntent.setType("image/*");

                        startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLARY_REQUEST);
                    }
                });


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentPopup(v);
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });
    }


    private void initializeUI() {

//        subCategory = findViewById(R.id.autotvSubCategory);

        img = findViewById(R.id.imag);

        btnWeight = findViewById(R.id.btnWeight);

        etPrice = findViewById(R.id.etProductPrice);
        etProductName = findViewById(R.id.etProductTitle);


//        subCategorytwo = findViewById(R.id.autotvSubCategoryTwo);
        etRemark = findViewById(R.id.etRemark);

        cbIsProductAvailable = findViewById(R.id.isProductAvailable);

        saveChanges = findViewById(R.id.btnProductSaveChanges);

        btn = findViewById(R.id.btn);
        saveChanges.setVisibility(View.GONE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {

            if (requestCode == GALLARY_REQUEST && resultCode == RESULT_OK) {

                progressDialog.setMessage("Uploading...");
                progressDialog.show();


                Uri imageUri = data.getData();

                byte[] thumb_byte = new byte[0];

                final String ProductName = etProductName.getText().toString();

                StorageReference filepath = mStorage.child(category).child(imagename);

                filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {


                        if (task.isSuccessful()) {


                            final String ImageURL = task.getResult().getDownloadUrl().toString();

                            DatabaseReference db = databaseReference.push();

//                            HashMap<String, Object> product = new HashMap<>();
//                            product.put("image", downurl);
//                            product.put("ProductName", ProductName);
//                            product.put("Weight", MapWeightPrice);
//                            product.put("Available", cbIsProductAvailable.isChecked());
//                            product.put("DeliveryCharge", etDelivery.getText().toString().trim());
//                            product.put("Remark", etRemark.getText().toString().trim());

                            String Remark=etRemark.getText().toString().trim();


                            HashMap<String, Object> product = Config.ProductMap(ImageURL,ProductName,Remark,cbIsProductAvailable.isChecked(),MapWeightPrice,category);


                            db.setValue(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Product added Successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Error Occoured Please try after some time", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            Log.d("Upload Fail ", task.getException().toString());
                            Toast.makeText(MainActivity.this, "Error " + task.getException().toString(), Toast.LENGTH_SHORT).show();

                        }

                    }
                });


            }
        } catch (Exception e) {
            Toast.makeText(this, "Error is : " + e, Toast.LENGTH_SHORT).show();
            TextView textView = findViewById(R.id.error);
            textView.setText("Error is : " + e);
        }

    }


    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_cart) {
            Intent intent = new Intent(MainActivity.this, MyCartActivity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.action_search) {
            Toast.makeText(this, "Coming soon...", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private PopupWindow popWindow2;

    private void CommentPopup(View v) {


        if (!MapWeightPrice.isEmpty()) {
            if(!editProductDetails) {
                MapWeightPrice.clear();
            }
        }
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the custom popup layout
        assert layoutInflater != null;
        final View inflatedView = layoutInflater.inflate(R.layout.add_available_quantities, null, false);

        final TextView tvQuantity = inflatedView.findViewById(R.id.tvQuantity);
        final TextView tvPreviousQuantity = inflatedView.findViewById(R.id.tvPreviousQuantity);

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Weight  :  MRP   :  Offer Price");

        Iterator it = previousMapWeightPrice.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            String s = pair.getValue().toString();
            String[] arr = s.split(",");
            stringBuffer.append("\n " + pair.getKey() + "");
            for (String s1 : arr) {
                stringBuffer.append("\t " + s1 + "\t");
            }

        }
        tvPreviousQuantity.setText(stringBuffer.toString());



        Button btnAdd = inflatedView.findViewById(R.id.addNewWeight);
        Button btnSubmit = inflatedView.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow2.dismiss();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.single_add_available_quantity);

                Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
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

                dialog.show();

            }
        });

        popWindow2 = new PopupWindow(inflatedView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
//        popWindow2.setAnimationStyle(R.style.PopUpAnimation);
        // set a background drawable with rounders corners
        popWindow2.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.et_background));
        // make it focusable to show the keyboard to enter in `EditText`
        popWindow2.setFocusable(true);
        popWindow2.update();
        // make it outside touchable to dismiss the popup window
        popWindow2.setOutsideTouchable(true);
        // show the popup at bottom of the screen and set some margin at bottom ie,
        popWindow2.showAtLocation(v, Gravity.CENTER, 10, 10);
    }

    public static String random() {
        Random rand = new Random();
        return rand.nextInt() + "" + rand.nextDouble();
    }

    private void saveChanges(){

        Toast.makeText(MainActivity.this, "Updated ", Toast.LENGTH_SHORT).show();

        String ImageURL = data.getString("Image_Path");
        Log.d("ImageURL",ImageURL);
        String editProductCategory = data.getString("category");
        Log.d("category",editProductCategory);
        String editProductKey = data.getString("editProductKey");
        Log.d("editProductKey",editProductKey);

        HashMap<String, Object> saveChanges = new HashMap<>();
//                        saveChanges.put("DeliveryCharge", etDelivery.getText().toString().trim());
//                        saveChanges.put("AvailableQuantity", MapWeightPrice);

        saveChanges.put("Weight", MapWeightPrice);
        saveChanges.put("Price", MapWeightPrice);

//                        saveChanges.put("image", image_path);
//                        saveChanges.put("Available", cbIsProductAvailable.isChecked());
//                        saveChanges.put("Remark", etRemark.getText().toString().trim());
//                        saveChanges.put("ProductName", etProductName.getText().toString().trim());
        String ProductName=etProductName.getText().toString().trim();
        String Remark=etRemark.getText().toString().trim();



//                        Config.PRODUCTS.child(editProductCategory).child(editProductKey).setValue(saveChanges);



        Log.d("before","HashMap");
        HashMap<String, Object> product = Config.ProductMap(ImageURL,ProductName,Remark,cbIsProductAvailable.isChecked(),MapWeightPrice,data.getString("category"));
        Log.d("middle","HashMap");
        Config.PRODUCTS.child(editProductCategory).child(editProductKey).setValue(product);
        Log.d("end","HashMap");
        Toast.makeText(MainActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
        finish();


    }
}
