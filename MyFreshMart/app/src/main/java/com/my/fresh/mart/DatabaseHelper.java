package com.my.fresh.mart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by intel on 17-Jan-18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Products.db";
    private static final String TABLE_NAME = "Product_Names";

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context=context;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+TABLE_NAME+"(category TEXT,name TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void savaData(String category, String name)
    {

        ContentValues contentValues=new ContentValues();
        contentValues.put("category",category);
        contentValues.put("name",name);
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        sqLiteDatabase.insert(TABLE_NAME,null,contentValues);

    }

    public void deleteData(String name)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME,"name = '"+name+"'",null);
    }

    public void updateData(String name,String city,int number)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("city",city);
        contentValues.put("number",number);

        sqLiteDatabase.update(TABLE_NAME,contentValues,"name = '"+name+"'",null);

    }

    public String searchName(String name)
    {
        String searchquery="SELECT category FROM "+TABLE_NAME+" WHERE name LIKE '"+name+"%' LIMIT 1";
        SQLiteDatabase sqLiteDatabase =this.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(searchquery,null);
        String resultcity="NA";

        if (cursor!=null)
        {
            do {
                cursor.moveToFirst();
                resultcity=cursor.getString(cursor.getColumnIndex("category"));

            }while (cursor.moveToNext());
            cursor.close();
        }
        return resultcity;
    }

    public StringBuffer getData()
    {
        String query="SELECT * FROM "+TABLE_NAME;
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(query,null);

        StringBuffer stringBuffer=new StringBuffer();

        try
        {

        if (cursor!=null)
        {
            cursor.moveToFirst();
            do {
                String category=cursor.getString(cursor.getColumnIndex("category"));
//                stringBuffer.append("category: "+category+ "\n");

                String name=cursor.getString(cursor.getColumnIndex("name"));
                stringBuffer.append(category+" : "+name+ "\n");


//                stringBuffer.append("_____________________\n");

            }while (cursor.moveToNext());
            cursor.close();
        }

        }catch (Exception e)
        {

        }
        return stringBuffer;

    }
    public List<String> getSingleData()
    {
        String query="SELECT * FROM "+TABLE_NAME;
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(query,null);

        List l=new ArrayList(1000);


//        StringBuffer stringBuffer=new StringBuffer();

        try
        {

        if (cursor!=null)
        {
            cursor.moveToFirst();
            do {
                String category=cursor.getString(cursor.getColumnIndex("category"));
//                stringBuffer.append("category: "+category+ "\n");

                String name=cursor.getString(cursor.getColumnIndex("name"));
//                stringBuffer.append(category+" : "+name+ "\n");
                l.add(name);

//                stringBuffer.append("_____________________\n");

            }while (cursor.moveToNext());
            cursor.close();
        }

        }catch (Exception e)
        {

        }
        return l;

    }

}
