package com.itrans.kurs.fragment;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.itrans.kurs.DBHelper;
import com.itrans.kurs.R;
import com.itrans.kurs.adapter.ImageGalleryAdapter;
import com.itrans.kurs.model.Wish;
import com.itrans.kurs.model.WishImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class WishGalleryFragment extends Fragment implements RedactImageFragment.OnFragmentInteractionListener{

    private static final String ARG_NAME = "name";
    private static final String ARG_DIR_NAME = "dir_name";

    private static final int TYPE_EDIT = 0;
    private static final int TYPE_SHARE = 1;

    public static List<Integer> selectedItemList;
    private FusedLocationProviderClient fusedLocationProviderClient;


    private static final int CAMERA_RESULT = 0;
    private Uri mOutputFileUri;
    private String mFileName;
    private Uri tempImageUri;

    private static String mName;
    private static String mDirName;

    private static ImageGalleryAdapter adapter;
    private static ArrayList<WishImage> wishData;
    private static DBHelper dbHelper;
    private static ArrayList<WishImage> deleteData;

    View childView;
    int childPosition;

    @Override
    public void onShareFragmentInteraction(String uri, String comment) {

    }

    public WishGalleryFragment() {
        // Required empty public constructor
    }


    public static WishGalleryFragment newInstance(String name, String dir_name) {
        WishGalleryFragment fragment = new WishGalleryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_DIR_NAME, dir_name);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mName = getArguments().getString(ARG_NAME);
            mDirName = getArguments().getString(ARG_DIR_NAME);
        }
        dbHelper = new DBHelper(getActivity());
        wishData = WishImage.getWishImages(dbHelper, mDirName);
        WishImage.showDatabase(dbHelper,"onCreate");
        deleteData = new ArrayList<>();
        selectedItemList = new ArrayList<>();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        setHasOptionsMenu(true);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wish_gallery, container, false);
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
                    if(adapter.isActionMode()){
                        adapter.setACTION_MODE(false);
                        updateOptionsMenu();
                        adapter.notifyDataSetChanged();
                        selectedItemList.clear();
                    }
                    else{
                        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    return true;
                }
                return false;
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
        final RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.rv_images);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gd = new GestureDetector(getActivity(),new GestureDetector.SimpleOnGestureListener(){

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    Log.d("TAG","SIngle tap");
                    if(adapter.isActionMode()){
                        View child = recyclerView.findChildViewUnder(e.getX(),e.getY());
                        int selectedPosition = recyclerView.getChildAdapterPosition(child);
                        if(selectedPosition!=-1){
                            addItemToSelectedList(selectedPosition);
                        }
                    }
                    else{
                        childView = recyclerView.findChildViewUnder(e.getX(),e.getY());
                        if(childView!=null){
                            childPosition = recyclerView.getChildAdapterPosition(childView);
                            ItemInformationFragment iif = ItemInformationFragment.newInstance(wishData.get(childPosition));
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.replace(R.id.frgmCont,iif,"item_information");
                            ft.addToBackStack("item_information");
                            ft.commit();
                        }
                    }
                    return  false;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    if(!adapter.isActionMode()){
                        adapter.setACTION_MODE(true);
                        View child = recyclerView.findChildViewUnder(e.getX(),e.getY());
                        child.setHapticFeedbackEnabled(true);
                        child.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

                        updateOptionsMenu();

                        int selectedPosition = recyclerView.getChildAdapterPosition(child);
                        if(selectedPosition!=-1){
                            addItemToSelectedList(selectedPosition);
                        }
                    }
                }
            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                gd.onTouchEvent(e);
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        adapter = new ImageGalleryAdapter(getActivity(),wishData);
        recyclerView.setAdapter(adapter);
    }


    private void updateOptionsMenu(){
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d("TAG","onCreateOptionsMenu");
        if(adapter.isActionMode()){
            inflater.inflate(R.menu.wish_gallery_action_mode_menu,menu);
        }else {
            inflater.inflate(R.menu.wish_gallery_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("TAG","onOptionsItemSelected");
        switch (item.getItemId()){
            case R.id.addNewImage:
                makeNewImage();
                return true;
            case R.id.delete_items:
                deleteSelectedItems();
                adapter.setACTION_MODE(false);
                updateOptionsMenu();
                return true;
            case R.id.share_items:
                shareSelectedItems();
                return true;
            case R.id.undoDelete:
                undoDelete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void undoDelete() {
        wishData.addAll(deleteData);
        adapter.notifyDataSetChanged();
        deleteData.clear();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
         if(requestCode == CAMERA_RESULT){
            if(resultCode!=0) {
                redactImage(this, tempImageUri, mOutputFileUri,"","",TYPE_EDIT);
            }
        }
    }

    private void makeNewImage(){
        if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    1);
        }
        else{
            openTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
            }
            else{
                Toast.makeText(getActivity(),"Do not have permissions",Toast.LENGTH_LONG);
            }
        }
    }

    private File createTemporaryFile(String part, String ext) throws IOException{
        File tempDir = Environment.getExternalStorageDirectory();
        tempDir = new File(tempDir.getAbsolutePath()+"/.temp");
        if(!tempDir.exists()){
            tempDir.mkdirs();
        }
        return File.createTempFile(part,ext,tempDir);
    }


    private void openTakePictureIntent(){
        mFileName = createUniqImageName();
        File file = new File(Wish.getDir_path()+File.separator+mDirName,mFileName);
        mOutputFileUri = Uri.fromFile(file);

        File photo = null;

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try{
            photo = this.createTemporaryFile("picture",".jpg");
            photo.delete();
        }catch(IOException e){
            Log.e("TAG","Can't create file to take picture!");
        }
        tempImageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,tempImageUri);
        startActivityForResult(intent,CAMERA_RESULT);
    }

    private String createUniqImageName(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "IMG"+timeStamp+"_.jpg";
        return fileName;
    }

    public static void redactImage(Fragment f, Uri tempImageUri,Uri mOutputFileUri, String price, String comment,int type){
        RedactImageFragment redactImageFragment = RedactImageFragment.newInstance(mOutputFileUri,tempImageUri,f.getTag(),price,comment,type);
        FragmentTransaction ft = f.getFragmentManager().beginTransaction();
        ft.replace(R.id.frgmCont,redactImageFragment);
        ft.addToBackStack("redactImage");
        ft.commit();
    }


    @Override
    public void onRedactFragmentInteraction(String uri, String price, String comment) {
        WishImage wi = new WishImage(mDirName,uri,price,comment);
        if(wishData.size()!=0) {
            wi.setmId(wishData.get(wishData.size() - 1).getmId() + 1);
        }
        else{
            wi.setmId(1);
        }
        saveImage(Uri.parse(uri),wi);
    }

    private void saveImage(final Uri imageUri,final WishImage wi){

        File file = new File(imageUri.getPath());
        try{
            if(!file.exists()){
                createImageFile(file);
            }

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location!=null){
                                double longitude = location.getLongitude();
                                double latitude = location.getLatitude();
                                geoTag(imageUri.getPath(),latitude,longitude);
                                wishData.add(wi);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
        catch(SecurityException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private void createImageFile(File file) throws IOException{
        file.createNewFile();

        getActivity().getContentResolver().notifyChange(tempImageUri,null);
        ContentResolver cr = getActivity().getContentResolver();
        Bitmap imageBitmap = android.provider.MediaStore.Images.Media.getBitmap(cr,tempImageUri);

        FileOutputStream fos = new FileOutputStream(file);
        imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
        fos.close();
    }

    public void geoTag(String filename, double latitude, double longitude){
        ExifInterface exif;

        try {
            exif = new ExifInterface(filename);
            String new_latitude = convert(latitude);
            String new_longitude = convert(longitude);

            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, new_latitude);
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, new_longitude);


            if (latitude > 0) {
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
            } else {
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
            }

            if (longitude > 0) {
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");
            } else {
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
            }

            exif.saveAttributes();

        } catch (IOException e) {
            Log.e("TAG", e.getLocalizedMessage());
        }

    }

    private String convert(double latitude){
        StringBuilder sb = new StringBuilder();
        latitude = Math.abs(latitude);
        final int degree = (int)latitude;
        latitude *= 60;
        latitude -= degree * 60.0d;
        final int minute = (int)latitude;
        latitude *= 60;
        latitude -= minute * 60.0d;
        final int second = (int)(latitude * 1000.0d);

        sb.setLength(0);
        sb.append(degree);
        sb.append("/1,");
        sb.append(minute);
        sb.append("/1,");
        sb.append(second);
        sb.append("/1000,");
        return sb.toString();
    }

    @Override
    public void onStop() {
        super.onStop();
        clearDeleteData();
        insertOrUpdate();
    }





    private void clearDeleteData() {
        if(deleteData.size()!=0) {
            for (WishImage wi : deleteData) {
                deleteWishFromDatabase(wi.getmId(), wi.getName());
                deleteWishImageFile(wi.getUrl());
            }
            deleteData.clear();
        }
    }

    private static void deleteWishFromDatabase(int id,String wishName){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(DBHelper.TABLE_WISHES,DBHelper.KEY_ID+"= ? AND "+DBHelper.KEY_WISH_NAME+"= ?",new String[]{""+id,wishName});
    }

    private static void deleteWishImageFile(String filePath){
        File file = new File(filePath);
        file.delete();
    }

    private void insertOrUpdate(){
        WishImage.showDatabase(dbHelper,"before insert");
        ContentValues cv = new ContentValues();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        for(WishImage wi:wishData){
            Log.d("TAG","insertOrUpdate "+wi.toString());
            cv.put(DBHelper.KEY_WISH_NAME,wi.getName());
            cv.put(DBHelper.KEY_IMAGE_URI,wi.getUrl());
            cv.put(DBHelper.KEY_IMAGE_PRICE,wi.getPrice());
            cv.put(DBHelper.KEY_IMAGE_COMMENT,wi.getComment());
            int id = database.update(DBHelper.TABLE_WISHES,cv,DBHelper.KEY_ID+" = ? AND "+DBHelper.KEY_WISH_NAME+" = ?",new String[]{""+wi.getmId(),wi.getName()});
            if(id == 0){
                Log.d("TAG","insert");
                database.insertWithOnConflict(DBHelper.TABLE_WISHES,null,cv,SQLiteDatabase.CONFLICT_REPLACE);
            }
        }
        WishImage.showDatabase(dbHelper,"after insert");
    }

    public static void deleteWish(WishImage wish){
        wishData.remove(wish);
        deleteData.add(wish);
        adapter.notifyDataSetChanged();
    }

    private void addItemToSelectedList(int childPosition){
        int itemId = wishData.get(childPosition).getmId();
        if(!selectedItemList.contains(itemId)){
            selectedItemList.add(itemId);
        }else{
            Iterator<Integer> i = selectedItemList.iterator();
            while(i.hasNext()){
                Integer id = i.next();
                if(id == itemId){
                    i.remove();
                }
            }

            if(selectedItemList.size()<1){
                adapter.setACTION_MODE(false);
                updateOptionsMenu();
            }
        }
        adapter.notifyDataSetChanged();
    }

    void deleteSelectedItems(){
        Iterator<Integer> i = selectedItemList.iterator();
        while(i.hasNext()){
            Integer id = i.next();
            deleteWish(getItemById(id));
        }
        selectedItemList.clear();
    }

    void shareSelectedItems(){
        ArrayList<Uri> selectedImages = new ArrayList<>();

        for(Integer i: selectedItemList){
            String imagePath = wishData.get(i).getUrl();
            selectedImages.add(Uri.fromFile(new File(imagePath)));
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,selectedImages);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, "Share images..."));

    }

    public static WishImage getItemById(int id){
        for (WishImage wish:wishData) {
            if(wish.getmId() == id){
                return wish;
            }
        }
        return null;
    }

}
