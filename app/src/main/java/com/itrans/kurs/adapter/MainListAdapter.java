package com.itrans.kurs.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.itrans.kurs.R;
import com.itrans.kurs.model.Wish;

import java.util.ArrayList;


public class MainListAdapter extends ArrayAdapter<Wish> {

    private Context context;
    private ArrayList<Wish> objects;

    public MainListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Wish> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.wish_list_item,null,false);
        }
        TextView item_text = (TextView) convertView.findViewById(R.id.item_text);
        Wish wish = objects.get(position);
        item_text.setText(wish.getName());
        return convertView;
    }
}
