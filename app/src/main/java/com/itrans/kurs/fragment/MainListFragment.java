package com.itrans.kurs.fragment;


import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.itrans.kurs.adapter.MainListAdapter;
import com.itrans.kurs.R;
import com.itrans.kurs.model.Wish;

import java.io.File;
import java.util.ArrayList;

public class MainListFragment extends ListFragment implements WishAddFragment.OnFragmentInteractionListener {
    ArrayList<Wish> data = new ArrayList<>();
    MainListAdapter adapter;
    int itemId;
    final int TYPE_ADD = 0;
    final int TYPE_EDIT = 1;

    final int MENU_EDIT = 1;
    final int MENU_DELETE = 2;

    private int editItemId;

    public static MainListFragment newInstance(ArrayList<Wish> wishList){
        MainListFragment fragment = new MainListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("wishList",(ArrayList<? extends Parcelable>) wishList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        data = getArguments().getParcelableArrayList("wishList");
        adapter = new MainListAdapter(getActivity(), R.layout.wish_list_item,data);
        registerForContextMenu(getListView());
        setListAdapter(adapter);Log.d("TAG","grabImageEnd");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0,MENU_EDIT,0,"EDIT");
        menu.add(0,MENU_DELETE,0,"DELETE");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int id = info.position;
        switch (item.getItemId()){
            case MENU_DELETE:
                deleteWish(id);
                return true;
            case MENU_EDIT:
                editItemId = id;
                showEditDialog();
                return true;
        }
        return super.onContextItemSelected(item);
    }




    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDetails(position);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_list,null);
        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });
        return v;
    }

    void showAddDialog(){
        WishAddFragment waf = WishAddFragment.newInstance(TYPE_ADD);
        waf.show(getFragmentManager(),"waf");
    }

    void showEditDialog(){
        Log.d("TAG","showEditDialog "+TYPE_EDIT);
        WishAddFragment waf = WishAddFragment.newInstance(TYPE_EDIT);
        waf.show(getFragmentManager(),"waf");
    }

    void showDetails(int index){
        WishGalleryFragment wishGallery = WishGalleryFragment.newInstance(data.get(index).getName(),data.get(index).getDir_name());
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frgmCont,wishGallery,"fragment_wish_gallery");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack("wish");
        ft.commit();
    }

    @Override
    public void onAddFragmentInteraction(String text) {
        Wish wish = new Wish(text);
        data.add(wish);
        createDirectory(wish.getDir_name());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onEditFragmentInteraction(String text) {
        data.get(editItemId).setName(text);
        adapter.notifyDataSetChanged();
    }

    public void createDirectory(String dir_id){
        File file = new File(Wish.getDir_path(), dir_id);
        if (!file.mkdirs()){
            Log.e("TAG", "Directory not created");
        }
        else{
            Log.d("TAG",Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).toString());
        }

    }

    private void deleteWish(int id){
        Wish delWish = data.get(id);
        data.remove(delWish);
        File f = new File(Wish.getDir_path()+File.separator+delWish.getDir_name());
        deleteWishDir(f);
        adapter.notifyDataSetChanged();
    }


    private void deleteWishDir(File dir_name) {
        if(dir_name.isDirectory()){
            for(File child: dir_name.listFiles()){
                deleteWishDir(child);
            }
        }
        dir_name.delete();
    }

}
