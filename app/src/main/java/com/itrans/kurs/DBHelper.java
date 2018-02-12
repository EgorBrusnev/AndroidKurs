package com.itrans.kurs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

    private int size;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "wishDB";
    public static final String TABLE_WISHES = "wishes";

    public static final String KEY_ID = "_id";
    public static final String KEY_IMAGE_URI = "image_url";
    public static final String KEY_WISH_NAME = "image_name";
    public static final String KEY_IMAGE_PRICE = "image_price";
    public static final String KEY_IMAGE_COMMENT = "image_comment";

    public DBHelper(Context context){
        super(context, DATABASE_NAME,null, DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_WISHES+"("+KEY_ID
                +" integer primary key autoincrement, "+KEY_IMAGE_URI+" text, " +KEY_WISH_NAME+" text, "+KEY_IMAGE_PRICE+" text, "+KEY_IMAGE_COMMENT+" text"+")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+TABLE_WISHES);
        onCreate(db);
    }

}
