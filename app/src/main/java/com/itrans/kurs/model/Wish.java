package com.itrans.kurs.model;


import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class Wish implements Parcelable{

    private String dir_name;
    private String name;
    private Date date;
    private static String dir_path = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES)+ File.separator+
            "WishNavigator";

    public static final Parcelable.Creator<Wish> CREATOR = new Parcelable.Creator<Wish>() {
        public Wish createFromParcel(Parcel in) {
            return new Wish(in);
        }

        public Wish[] newArray(int size) {

            return new Wish[size];
        }

    };

    @Override
    public String toString() {
        return "Wish{" +
                "dir_name='" + dir_name + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public Wish(Parcel in){
    }

    public Wish(String name) {
        this.name = name;
        this.dir_name = UUID.randomUUID().toString().substring(0,8);
    }

    public Wish(String name, String dir_name){
        this.name = name;
        this.dir_name = dir_name;
    }

    public static String getDir_path() {
        return dir_path;
    }

    public static void setDir_path(String dir_path) {
        Wish.dir_path = dir_path;
    }

    public  String getDir_name() {
        return dir_name;
    }

    public void setDir_name(String dir_name) {
        this.dir_name = dir_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate() {
        this.date = new Date();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
