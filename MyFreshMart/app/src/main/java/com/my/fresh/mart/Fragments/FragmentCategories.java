package com.my.fresh.mart.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.my.fresh.mart.Config;
import com.my.fresh.mart.MainActivity;
import com.my.fresh.mart.R;

import java.util.ArrayList;

/**
 * Created by intel on 09-Dec-17.
 */

public class FragmentCategories extends Fragment {


    ListView CategoriesListView;
    Boolean upload=false;
    ArrayList<String> CategoriesArraylist;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_categories,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        CategoriesArraylist = new ArrayList();
        CategoriesArraylist.add("Fruits");//0
        CategoriesArraylist.add("Vegetables");//1
        CategoriesArraylist.add("Dry Fruits");//2


        CategoriesViewAdater categoriesViewAdater = new CategoriesViewAdater();
        CategoriesListView = view.findViewById(R.id.lvCategory);
        CategoriesListView.setAdapter(categoriesViewAdater);

        CategoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();
//                Toast.makeText(.this, selectedItem, Toast.LENGTH_SHORT).show();


                if (i == 0)//Fruits
                {

                    if (upload) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.putExtra("CategoryValue", Config.FRUITS);
                        startActivity(intent);
                    } else {


                        FragmentProductList ldf = new FragmentProductList();
                        Bundle args = new Bundle();
                        args.putString("CategoryValue", Config.FRUITS);
                        ldf.setArguments(args);

                        getFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right
                        ).addToBackStack("MainStack").replace(R.id.containerCustomerActivity, ldf).commit();
                    }


                } else if (i == 1) //Vegetables
                {

                    if (upload) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.putExtra("CategoryValue", Config.VEGETABLES);
                        startActivity(intent);
                    } else {


                        FragmentProductList ldf = new FragmentProductList();
                        Bundle args = new Bundle();
                        args.putString("CategoryValue", Config.VEGETABLES);
                        ldf.setArguments(args);

                        getFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right
                        ).addToBackStack("MainStack").replace(R.id.containerCustomerActivity, ldf).commit();
                    }


                } else if (i == 2)//Dry Fruits
                {

                    if (upload) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.putExtra("CategoryValue", Config.DRY_FRUITS);
                        startActivity(intent);
                    } else {


                        FragmentProductList ldf = new FragmentProductList();
                        Bundle args = new Bundle();
                        args.putString("CategoryValue", Config.DRY_FRUITS);
                        ldf.setArguments(args);

                        getFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right
                        ).addToBackStack("MainStack").replace(R.id.containerCustomerActivity, ldf).commit();
                    }


                }


            }
        });


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

