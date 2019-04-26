package com.my.fresh.mart.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.my.fresh.mart.Config;
import com.my.fresh.mart.Model.Products;
import com.my.fresh.mart.ProductDescrpitionActivity;
import com.my.fresh.mart.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.my.fresh.mart.ParidizActivity.FIREBASE_UI_SIGN_IN;

/**
 * Created by intel on 05-Dec-17.
 */

public class FragmentProductList extends Fragment {

    RecyclerView recyclerView;
    Query mQuery;
    ProgressDialog progressDialog;
    ArrayList<ProductPrice> AvailableCategoryArraylist ;

    //Firebase

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.products_recycler_view, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        String value = getArguments().getString("CategoryValue");
        String searchQuery = getArguments().getString("searchQuery");


        final String category = value;

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        recyclerView = view.findViewById(R.id.rvProducts);

//        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mDatabase = Config.PRODUCTS.child(category);

        if (searchQuery==null||searchQuery.equals(""))
        {
            mQuery = mDatabase.orderByKey();
        }else {
            mQuery = mDatabase.orderByChild("ProductName").startAt(searchQuery);
        }


        int total;
        try {

            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("data",dataSnapshot.toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            FirebaseRecyclerAdapter<Products, ProductsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Products, ProductsViewHolder>(
                    Products.class,
                    R.layout.single_products,
                    ProductsViewHolder.class,
                    mQuery
            ) {
                @Override
                protected void populateViewHolder(final ProductsViewHolder viewHolder, final Products model, int position) {
                    progressDialog.dismiss();
                    final String ProductKey = getRef(position).getKey();
                    viewHolder.setProductName(model.getProductName());

                    viewHolder.setImage(getContext(), model.getImage());
                    String price[]=model.getPrice().split(",");
                    viewHolder.setPrice("MRP : ₹ <strike> "+price[0]+" </strike>  <b> "+price[1]+"</b>");
                    viewHolder.setWeight(model.getWeight());
                    if (!model.isAvailability())
                    {
                        viewHolder.mView.setVisibility(View.GONE);
                    }

                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), ProductDescrpitionActivity.class);
                            intent.putExtra("ProductName", model.getProductName());
                            intent.putExtra("Image", model.getImage());
                            intent.putExtra("ProductKey", ProductKey);
                            intent.putExtra("Category", category);
                            intent.putExtra("Remark", model.getRemark());
                            /*String arr[]=model.getPrice().split(",");
                            intent.putExtra("OfferPrice",arr[0]);
                            intent.putExtra("MRPPrice",arr[1]);
                            */
                            try {
                                intent.putExtra("OfferPrice",viewHolder.AvailableCategoryArraylistHolder.get(viewHolder.arrIndex).OfferPrice);
                                intent.putExtra("MRPPrice",viewHolder.AvailableCategoryArraylistHolder.get(viewHolder.arrIndex).MRP);
                                intent.putExtra("Weight",viewHolder.AvailableCategoryArraylistHolder.get(viewHolder.arrIndex).Weight);

                            }catch (Exception e)
                            {
                                String [] arr=model.getPrice().split(",");
                                intent.putExtra("OfferPrice",arr[0]);
                                intent.putExtra("MRPPrice",arr[1]);
                                intent.putExtra("Weight",model.getWeight());
                            }
                            startActivity(intent);
                        }
                    });

                    viewHolder.availableQuantity.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            DialogAvailableQuantities(v,ProductKey,model.getProductName(),category,viewHolder);


                        }
                    });

                    viewHolder.btnAddtoCart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {

                            //todo show loading animation

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user == null) {
                                startActivityForResult(Config.FIREBASE_LOGIN(), FIREBASE_UI_SIGN_IN);
                            } else {

                                String ImageUrl=model.getImage();
                                String ProductName=model.getProductName();
                                //String ProductWeight=model.getWeight();


                                String ProductWeight;
                                String ProductMRPPrice;
                                String ProductOfferPrice;

                                try {
                                    ProductWeight = viewHolder.AvailableCategoryArraylistHolder.get(viewHolder.arrIndex).Weight;
                                    ProductMRPPrice = viewHolder.AvailableCategoryArraylistHolder.get(viewHolder.arrIndex).MRP;
                                    ProductOfferPrice = viewHolder.AvailableCategoryArraylistHolder.get(viewHolder.arrIndex).OfferPrice;

                                }catch (Exception e)
                                {
                                    String [] arr=model.getPrice().split(",");
                                    ProductMRPPrice=arr[0];
                                    ProductOfferPrice=arr[1];
                                    ProductWeight=model.getWeight();
                                }

                                final HashMap mapCart=Config.CartMap(ProductKey,ImageUrl,ProductName,ProductOfferPrice,ProductMRPPrice,ProductWeight,"1",category,Config.getCurrentTime());

                                final DatabaseReference dbAddToCarts = FirebaseDatabase.getInstance().getReference().child("Paridiz").child("Cart").child(user.getUid());
                                dbAddToCarts.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (!dataSnapshot.hasChild(ProductKey)) {
                                            dbAddToCarts.child(ProductKey).setValue(mapCart).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Snackbar.make(view, "Product added to cart", Snackbar.LENGTH_LONG).show();
                                                }
                                            });

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

                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.blink);
                    viewHolder.btnAddtoCart.startAnimation(animation);
                    viewHolder.btnAddtoCart.startAnimation(animation);
                }
            };
            recyclerView.setAdapter(firebaseRecyclerAdapter);

        } catch (Exception e) {
            Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();
        }
    }


    public static class ProductsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        int arrIndex=0;
        Button btnAddtoCart;
        RelativeLayout availableQuantity;
        ArrayList<ProductPrice> AvailableCategoryArraylistHolder =new ArrayList<>();


        public ProductsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            btnAddtoCart = mView.findViewById(R.id.btnAddToCart);
            availableQuantity = mView.findViewById(R.id.availableQuantity);
        }


        public void setProductName(String Name) {
            TextView ProductName= mView.findViewById(R.id.tvProductName);
            ProductName.setText(Name);
        }

        public void setWeight(String weight)
        {
            TextView ProductWeight0= mView.findViewById(R.id.tvWeight);
            ProductWeight0.setText(Html.fromHtml(weight));
        }
        public void setPrice(String price)
        {
            TextView ProductPrice= mView.findViewById(R.id.tvMRP);
            ProductPrice.setText(Html.fromHtml(price));
        }

        public String getWeight()
        {
            TextView ProductWeight= mView.findViewById(R.id.tvWeight);
            return ProductWeight.getText().toString().trim();
        }
        public String getPrice()
        {
            TextView ProductPrice= mView.findViewById(R.id.tvMRP);
            return ProductPrice.getText().toString().trim();
        }



        public void setImage(Context ctx, String image) {
            ImageView imageView = mView.findViewById(R.id.productImage);

            Glide.with(ctx)
                    .load(image)
                    .placeholder(R.drawable.placehoder_image)
                    .into(imageView);

        }

    }



    private void DialogAvailableQuantities(View v, final String ProductKey, String ProductName, String category, final ProductsViewHolder viewHolder) {


        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_available_quantities);

        TextView tvProductName=dialog.findViewById(R.id.tvProductName);
        tvProductName.setText(ProductName);

        AvailableCategoryArraylist = new ArrayList<>();


        final ListView AvailableQuantities=dialog.findViewById(R.id.lvAvailableQuantities);
        final CategoriesViewAdater categoriesViewAdater = new CategoriesViewAdater();

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

                    AvailableCategoryArraylist.add(new ProductPrice(s[0],s[1],pair.getKey().toString()));
                    System.out.println(pair.getKey() + " = " + pair.getValue());
                    it.remove(); // avoids a ConcurrentModificationException
                }

                AvailableQuantities.setAdapter(categoriesViewAdater);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        AvailableQuantities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();

                Log.i("selectedItem",selectedItem);

                AvailableCategoryArraylist.get(i);
                viewHolder.arrIndex=i;
                viewHolder.AvailableCategoryArraylistHolder=new ArrayList<>(AvailableCategoryArraylist);
                viewHolder.setWeight(AvailableCategoryArraylist.get(i).getWeight());
                viewHolder.setPrice(AvailableCategoryArraylist.get(i).getMRP()+"   "+AvailableCategoryArraylist.get(i).getOfferPrice());
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

    public class CategoriesViewAdater extends BaseAdapter {

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

    public class ProductPrice{
        public String MRP;
        public String OfferPrice;
        public String Weight;


        public String toString()
        {
            return "<b> " +Weight + "</b> - " +" MRP : ₹ <strike> " +MRP+" </strike>  <b> "+OfferPrice+"</b>" +"";
        }
        ProductPrice(String mrp, String offerPrice, String weight) {
            MRP = mrp;
            OfferPrice = offerPrice;
            Weight = weight;
        }


        public String getMRP() {
            String s="MRP : ₹ <Strike> "+MRP+"</Strike>";
            return s;
        }

        public void setMRP(String MRP) {
            this.MRP = MRP;
        }

        public String getOfferPrice() {
            String s="<b>"+OfferPrice+"</b>";
            return s;
        }

        public void setOfferPrice(String offerPrice) {
            OfferPrice = offerPrice;
        }

        public String getWeight() {
            return Weight;
        }

        public void setWeight(String weight) {
            Weight = weight;
        }
    }
}
