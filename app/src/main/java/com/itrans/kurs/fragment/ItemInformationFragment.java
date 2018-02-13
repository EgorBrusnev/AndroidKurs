package com.itrans.kurs.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.itrans.kurs.R;
import com.itrans.kurs.model.WishImage;

import java.io.File;
import java.io.IOException;


public class ItemInformationFragment extends Fragment implements RedactImageFragment.OnFragmentInteractionListener {


    private static final String ARG_WISH_ITEM = "wish_item";

    private static final int TYPE_EDIT = 0;
    private static final int TYPE_SHARE = 1;


    private Double dLongitude,dLatitude;

    private WishImage mWishItem;

    private ImageView item_image;
    private TextView item_price;
    private TextView item_comment;
    private TextView item_location;
    private Button navigate_button;

    private ExifInterface exif;

    private boolean isShare = false;


    public ItemInformationFragment() {
        // Required empty public constructor
    }


    public static ItemInformationFragment newInstance(WishImage wishItem){
        ItemInformationFragment fragment = new ItemInformationFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_WISH_ITEM,wishItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.item_information_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_item_share:
                isShare = true;
                shareImage();
                return true;
            case R.id.menu_item_edit:
                isShare = false;
                WishGalleryFragment.redactImage(this,null,Uri.parse(mWishItem.getUrl()),mWishItem.getPrice(),mWishItem.getComment(),TYPE_EDIT);
                return true;
            case R.id.menu_navigation:
                startNavigation();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startNavigation() {
        NavigatorFragment nf = NavigatorFragment.newInstance(dLatitude,dLongitude);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frgmCont,nf,"navigator_fragment");
        ft.addToBackStack("");
        ft.commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mWishItem = getArguments().getParcelable(ARG_WISH_ITEM);
        }
        setHasOptionsMenu(true);
    }

    private RequestListener<String, GlideDrawable> requestListener = new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            Log.e("TAG","Glide Exception "+e.toString());
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            return false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_item_information,container,false);
        item_price = v.findViewById(R.id.item_price);
        item_comment = v.findViewById(R.id.item_comment);
        item_image = v.findViewById(R.id.item_image);

        if(!mWishItem.getPrice().equals("")){
            item_price.setText("Price: "+mWishItem.getPrice());
        }
        else{
            item_price.setVisibility(View.GONE);
        }

        if(!mWishItem.getComment().equals("")){
            item_comment.setText("Your comment: "+mWishItem.getComment());
        }
        else{
            item_comment.setVisibility(View.GONE);
        }

        item_image.post(new Runnable() {
            @Override
            public void run() {
                Glide.with(getActivity())
                        .load(mWishItem.getUrl())
                        .listener(requestListener)
                        .error(android.R.color.holo_red_light)
                        .fallback(android.R.color.holo_orange_light)
                        .into(item_image);
            }
        });
        try{
            exif = new ExifInterface(mWishItem.getUrl());
            String lat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String lon = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String lat_ref = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String lon_ref = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            if(lat_ref.equals("N")){
                dLatitude = WishImage.convertToDegree(lat);
            }
            else{
                dLatitude = 0-WishImage.convertToDegree(lat);
            }
            if(lon_ref.equals("E")){
                dLongitude = WishImage.convertToDegree(lon);
            }
            else{
                dLongitude = 0-WishImage.convertToDegree(lon);
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return v;
    }

    private void previewImageBitmap(){


        item_image.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                item_image.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int targetW = item_image.getWidth();
                int targetH = item_image.getHeight();
                BitmapFactory.Options bounds = new BitmapFactory.Options();
                bounds.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mWishItem.getUrl(),bounds);
                int photoW = bounds.outWidth;
                int photoH = bounds.outHeight;
                int scale;
                if(targetH == 0){
                    scale = photoW/targetW;
                }
                else {
                    scale = Math.min(photoW / targetW, photoH / targetH);
                }

                bounds.inJustDecodeBounds = false;
                bounds.inSampleSize = scale;
                bounds.inPurgeable = true;

                Bitmap bitmap = BitmapFactory.decodeFile(mWishItem.getUrl(),bounds);
                item_image.setImageBitmap(bitmap);
            }
        });
    }

    private void shareImage(){
        WishGalleryFragment.redactImage(this,null,Uri.parse(mWishItem.getUrl()),mWishItem.getPrice(),mWishItem.getComment(),TYPE_SHARE);
    }

    @Override
    public void onRedactFragmentInteraction(String imageDir, String price, String comment) {
        mWishItem.setComment(comment);
        mWishItem.setPrice(price);
    }

    @Override
    public void onShareFragmentInteraction(String uri, String comment) {
        createShareIntent(getActivity(), uri,comment);
    }

    public static void createShareIntent(Context context, String imageDir, String comment){
        File file = new File(imageDir);
        Uri uri = Uri.fromFile(file);
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_STREAM,uri);
        share.putExtra(Intent.EXTRA_TEXT,comment);
        share.setType("image/*");
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(share,"Share Image"));
    }
}
