package com.itrans.kurs.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.itrans.kurs.R;
import com.itrans.kurs.fragment.WishGalleryFragment;
import com.itrans.kurs.model.WishImage;

import java.util.ArrayList;

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.ImageViewHolder> {
    public class ImageViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        CheckBox checkBox;

        ImageViewHolder(View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_photo);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }


    private ArrayList<WishImage> images;
    private Context mContext;
    private boolean isACTION_MODE;

    public ImageGalleryAdapter(Context context,ArrayList<WishImage> images){
        this.images = images;
        mContext = context;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.wish_gallery_item,parent,false);
        ImageViewHolder ivh = new ImageViewHolder(v);
        return ivh;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        WishImage wishImage = images.get(position);
        ImageView imageView = holder.imageView;
        if(isACTION_MODE){
            setIndicatingActionMode(holder);
            setItemSelected(holder,isInSelectedItemList(wishImage.getmId()));
        }
        else{
            setIndicatingNormalMode(holder);
        }
        Glide.with(mContext)
                .load(wishImage.getUrl())
                .into(imageView);
    }

    private void setIndicatingNormalMode(ImageViewHolder holder) {
        holder.itemView.findViewById(R.id.checkbox).setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void setACTION_MODE(boolean ACTION_MODE) {
        isACTION_MODE = ACTION_MODE;
    }

    public boolean isActionMode(){
        return isACTION_MODE;
    }

    private void setIndicatingActionMode(ImageViewHolder holder){
        holder.itemView.findViewById(R.id.checkbox).setVisibility(View.VISIBLE);
    }

    private boolean isInSelectedItemList(int id){
        return WishGalleryFragment.selectedItemList.contains(id);
    }
    private void setItemSelected(ImageViewHolder holder, boolean isSelected){
        CheckBox checkBox = (CheckBox) holder.itemView.findViewById(R.id.checkbox);
        checkBox.setChecked(isSelected);
    }
}
