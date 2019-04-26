package com.my.fresh.mart;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by intel on 17-Jan-18.
 */

public class AsyncTaskDownloadDB extends AsyncTask<String, Void, String> {

    String abc = "";
    int count = 1;
    DatabaseHelper databaseHelper;
    private Context context;
    private TextView textViewDb;

    public AsyncTaskDownloadDB(Context context) {
        this.context = context;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        Toast.makeText(context, "waiting to download db", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected String doInBackground(String... strings) {

        LinkedList<String> categoryList = new LinkedList<>();
        categoryList.add("Fruits");//0
        categoryList.add("Vegetables");//1
        categoryList.add("Dry Fruits");//2

        Iterator<String> listIterator = categoryList.iterator();


        databaseHelper = new DatabaseHelper(context);
        databaseHelper.getWritableDatabase();

        final StringBuffer stringBuffer = databaseHelper.getData();
//        textViewDb.setText(stringBuffer.toString());

        if (checkDataBase()) {


            checkDataBase();
            while (listIterator.hasNext()) {
                String cat = listIterator.next();
                Config.PRODUCTS.child(cat).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

//                    checkDataBase();

                        for (DataSnapshot suggestiondataSnapshot : dataSnapshot.getChildren()) {
                            String category = suggestiondataSnapshot.child("Category").getValue(String.class);
                            String name = suggestiondataSnapshot.child("ProductName").getValue(String.class);
//                            textViewDb.append(count + ") " + category + "  :  " + name + "\n");
//                            count++;
                            databaseHelper.savaData(category, name);
//                            Toast.makeText(context, "DB exists", Toast.LENGTH_SHORT).show();
                        }

//                        checkDataBase();
//                        else
//                        {
//
////                            Toast.makeText(context, "DB not exists", Toast.LENGTH_SHORT).show();
//                        }
//                        Toast.makeText(MyCartActivity.this, ""+subCategoryOne, Toast.LENGTH_SHORT).show();
//                        abc=abc+"\n"+category+":"+name;


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        } else {
//            textViewDb.setText(stringBuffer);
        }

        return abc;


    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
//        Toast.makeText(context, ""+s, Toast.LENGTH_SHORT).show();
//        textViewDb.setText("s"+s);

    }

    private boolean checkDataBase() {

        boolean dbc;
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase("/data/data/com.paridiz.kart/databases/Products", null,
                    SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
        } catch (SQLiteException e) {
            // database doesn't exist yet.
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String count = "SELECT count(*) FROM Product_Names";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if (icount > 0)
        //leave {
        {
            dbc = false;
//            Toast.makeText(context, "db emty", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(context, "db not emty", Toast.LENGTH_SHORT).show();
            dbc = true;
        }
        //populate table

//        return checkDB != null;
        return dbc;

    }
}
