package com.my.fresh.mart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class CategoriesActivity extends AppCompatActivity {

    ListView CategoriesListView;
    Boolean upload, admin;
    ArrayList<String> CategoriesArraylist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        Bundle data = getIntent().getExtras();
        admin = false;
        upload = data.getBoolean("upload");
        admin = data.getBoolean("admin");

        CategoriesArraylist = new ArrayList();
        CategoriesArraylist.add("Fruits");//0
        CategoriesArraylist.add("Vegetables");//1
        CategoriesArraylist.add("Dry Fruits");//2


        CategoriesViewAdater categoriesViewAdater = new CategoriesViewAdater();
        CategoriesListView = findViewById(R.id.lvCategory);
        CategoriesListView.setAdapter(categoriesViewAdater);

        CategoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();
                if (i == 0)//Fruits
                {
                    if (upload) {
                        Intent intent = new Intent(CategoriesActivity.this, MainActivity.class);
                        intent.putExtra("CategoryValue", Config.FRUITS);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(CategoriesActivity.this, ProductsPatanjaliStoreActivity.class);
                        if (admin) {
                            intent.putExtra("admin", true);
                        }
                        intent.putExtra("CategoryValue", Config.FRUITS);
                        startActivity(intent);
                    }

                } else if (i == 1) //Vegetables
                {

                    if (upload) {
                        Intent intent = new Intent(CategoriesActivity.this, MainActivity.class);
                        intent.putExtra("CategoryValue", Config.VEGETABLES);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(CategoriesActivity.this, ProductsPatanjaliStoreActivity.class);
                        if (admin) {
                            intent.putExtra("admin", true);
                        }
                        intent.putExtra("CategoryValue", Config.VEGETABLES);
                        startActivity(intent);
                    }



                } else if (i == 2)//Dry Fruits
                {

                    if (upload) {
                        Intent intent = new Intent(CategoriesActivity.this, MainActivity.class);
                        intent.putExtra("CategoryValue", Config.DRY_FRUITS);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(CategoriesActivity.this, ProductsPatanjaliStoreActivity.class);
                        if (admin) {
                            intent.putExtra("admin", true);
                        }
                        intent.putExtra("CategoryValue", Config.DRY_FRUITS);
                        startActivity(intent);
                    }
                }


            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public class CategoriesViewAdater extends BaseAdapter {

        @Override
        public int getCount() {
            return CategoriesArraylist.size();
        }

        @Override
        public Object getItem(int i) {
            return CategoriesArraylist.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            LayoutInflater layoutInflater = getLayoutInflater();
            View view1 = layoutInflater.inflate(R.layout.single_category_item, null);

            TextView txtSingleItem = view1.findViewById(R.id.tvListItem);
            txtSingleItem.setText(CategoriesArraylist.get(i));

            return view1;
        }
    }
}
