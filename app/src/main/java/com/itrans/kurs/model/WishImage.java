package com.itrans.kurs.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.itrans.kurs.DBHelper;

import java.util.ArrayList;


public class WishImage implements Parcelable {

    private int mId;
    private String mName;
    private String mUrl;
    private String price;
    private String comment;

    WishImage(){
        mName = "test";
    }

    public WishImage(String url, String name) {
        mName = name;
        mUrl = url;
    }

    public WishImage(int mId, String mName, String mUrl, String price, String comment) {
        this.mId = mId;
        this.mName = mName;
        this.mUrl = mUrl;
        this.price = price;
        this.comment = comment;
    }

    public WishImage(String mName, String mUrl, String price, String comment) {
        this.mName = mName;
        this.mUrl = mUrl;
        this.price = price;
        this.comment = comment;
    }

    protected WishImage(Parcel in) {
        mName = in.readString();
        mUrl = in.readString();
    }

    public static final Creator<WishImage> CREATOR = new Creator<WishImage>() {
        @Override
        public WishImage createFromParcel(Parcel in) {
            return new WishImage(in);
        }

        @Override
        public WishImage[] newArray(int size) {
            return new WishImage[size];
        }
    };

    public String getName() {
        return mName;
    }

    public void setName(String mTitle) {
        this.mName = mTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public static ArrayList<WishImage> getWishImages(DBHelper dbHelper, String name){
        int tId;
        String tName,tUrl,tPrice,tComment;
        ArrayList<WishImage> wishImages = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_WISHES,null,DBHelper.KEY_WISH_NAME+"=\""+name+"\"",null,null,null,null);
        if(cursor.moveToFirst()){
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_WISH_NAME);
            int urlIndex = cursor.getColumnIndex(DBHelper.KEY_IMAGE_URI);
            int priceIndex = cursor.getColumnIndex(DBHelper.KEY_IMAGE_PRICE);
            int commentIndex = cursor.getColumnIndex(DBHelper.KEY_IMAGE_COMMENT);
            do{
                tId = cursor.getInt(idIndex);
                tName = cursor.getString(nameIndex);
                tUrl = cursor.getString(urlIndex);
                tPrice = cursor.getString(priceIndex);
                tComment = cursor.getString(commentIndex);
                WishImage wi = new WishImage(tId,tName,tUrl,tPrice,tComment);
                wishImages.add(wi);
            }while(cursor.moveToNext());
        }
        else{
            Log.d("TAG","EMPTY");
        }
        cursor.close();
        return wishImages;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mUrl);
    }

    @Override
    public String toString() {
        return "WishImage{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", mUrl='" + mUrl + '\'' +
                ", price='" + price + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }

    public static Double convertToDegree(String stringDMS){
        Double result;
        String[] DMS = stringDMS.split(",",3);
        String[] stringD = DMS[0].split("/",2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double D = D0/D1;
        String[] stringM = DMS[1].split("/",2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double M = M0/M1;
        String[] stringS = DMS[2].split("/",2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double S = S0/S1;
        Log.d("TAG",D+" "+M+" "+S);
        result = new Double(D+(M/60)+(S/3600));
//        result = Math.round(result*10000000.0)/10000000.0;
        return result;
    }

    public static void showDatabase(DBHelper dbHelper,String tag){
        int tId;
        String tName,tUrl,tPrice,tComment;
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_WISHES,null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_WISH_NAME);
            int urlIndex = cursor.getColumnIndex(DBHelper.KEY_IMAGE_URI);
            int priceIndex = cursor.getColumnIndex(DBHelper.KEY_IMAGE_PRICE);
            int commentIndex = cursor.getColumnIndex(DBHelper.KEY_IMAGE_COMMENT);
            do{
                tId = cursor.getInt(idIndex);
                tName = cursor.getString(nameIndex);
                tUrl = cursor.getString(urlIndex);
                tPrice = cursor.getString(priceIndex);
                tComment = cursor.getString(commentIndex);
                Log.d("TAG",tag+": "+tId+" "+tName+" "+tUrl+" "+tPrice);
            }while(cursor.moveToNext());
        }
        else{
            Log.d("TAG","EMPTY");
        }
        cursor.close();
    }
    public static void removeAll(DBHelper dbHelper){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(DBHelper.TABLE_WISHES,null,null);
    }
}
