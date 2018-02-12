package com.itrans.kurs;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.itrans.kurs.fragment.MainListFragment;
import com.itrans.kurs.model.Wish;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    SharedPreferences sPref;
    ArrayList<Wish> wishList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.INTERNET},1);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        load_pref();
//        clear_pref();
        MainListFragment list = MainListFragment.newInstance(wishList);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.frgmCont,list,"main_list_fragment");
        ft.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        save_pref();
    }

    public void save_pref(){
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putInt("size",wishList.size());
        int size = wishList.size();
        for (int i = 0; i < size; i++) {
            Wish wish = wishList.get(i);
            editor.putString("name"+i,wish.getName());
            editor.putString("dir_id"+i,wish.getDir_name());
        }
        editor.apply();
    }
    public void load_pref(){
        sPref = getPreferences(MODE_PRIVATE);
        int size = sPref.getInt("size",0);
        for (int i = 0; i < size; i++) {
            String name = sPref.getString("name"+i,"");
            String dir_id = sPref.getString("dir_id"+i,"");
            Wish wish = new Wish(name,dir_id);
            wishList.add(wish);
        }
    }
    public void clear_pref(){
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.clear();
        editor.apply();
    }

}
