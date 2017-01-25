package com.pallefire.b_34jsoncontactsparsing1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mirzaaqibbeg on 25-01-2017.
 */

public class MyDatabase {
    private MyHelper myHelper;
    private SQLiteDatabase sqLiteDatabase;
    public MyDatabase(Context c){
        myHelper=new MyHelper (c,"techpalle.db",null,1);
    }
    public void open(){
        sqLiteDatabase=myHelper.getWritableDatabase();
    }public void close(){
        sqLiteDatabase.close();
    }


    public void insert(String name, String email, String mobile){
        ContentValues contentValues=new ContentValues();
        contentValues.put("name",name);
        contentValues.put("email",email);
        contentValues.put("mobile",mobile);
        sqLiteDatabase.insert("contacts",null,contentValues);
    }
    public Cursor queryContacts(){
        Cursor c=null;
        c=sqLiteDatabase.query("contacts",null,null,null,null,null,null);
        return c;
    }

    private class MyHelper extends SQLiteOpenHelper{

        public MyHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public MyHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
            super(context, name, factory, version, errorHandler);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("create table contacts(_id integer primary key, name text, email text, mobile text);");

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}
