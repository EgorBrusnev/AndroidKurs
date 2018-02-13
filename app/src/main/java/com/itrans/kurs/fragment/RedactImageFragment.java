package com.itrans.kurs.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.itrans.kurs.IInkApplication;
import com.itrans.kurs.R;
import com.myscript.iink.Configuration;
import com.myscript.iink.ContentPackage;
import com.myscript.iink.ContentPart;
import com.myscript.iink.Editor;
import com.myscript.iink.Engine;
import com.myscript.iink.IEditorListener;
import com.myscript.iink.MimeType;
import com.myscript.iink.uireferenceimplementation.EditorView;
import com.myscript.iink.uireferenceimplementation.InputController;

import java.io.File;
import java.io.IOException;


public class RedactImageFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_TEMP_IMAGE = "temp_image";
    private static final String ARG_IMAGE_URI = "image_uri";
    private static final String ARG_FRAGMENT_TAG = "tag";
    private static final String ARG_IMAGE_PRICE = "price";
    private static final String ARG_IMAGE_COMMENT = "comment";
    private static final String ARG_TYPE = "edit_type";

    private static final int TYPE_EDIT = 0;
    private static final int TYPE_SHARE = 1;

    private Engine engine;
    private ContentPackage contentPackage;
    private ContentPart contentPart;
    private EditorView editorView;

    private Uri tempImageUri;
    private String mFragmentTag;
    private Uri mImageUri;
    private Bitmap imageBitmap;

    private ImageView image_preview;
    private EditText input_price;
    private EditText input_comment;

    private String price;
    private String comment;
    private int mEditType;


    private OnFragmentInteractionListener mListener;

    public RedactImageFragment() {
        // Required empty public constructor
    }

    public static RedactImageFragment newInstance(Uri uri, Uri tempImageUri,String tag,String price,String comment,int type) {
        RedactImageFragment fragment = new RedactImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TEMP_IMAGE, tempImageUri);
        args.putString(ARG_FRAGMENT_TAG,tag);
        args.putParcelable(ARG_IMAGE_URI,uri);
        args.putString(ARG_IMAGE_PRICE,price);
        args.putString(ARG_IMAGE_COMMENT,comment);
        args.putInt(ARG_TYPE,type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.redact_image_menu,menu);
        menu.findItem(R.id.menu_check_submit).setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_check_submit:
                submit_wish_item();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void convertPrice(){
        Editor editor = editorView.getEditor();
        try {
            String text = editor.export_(editor.getRootBlock(), MimeType.TEXT);
            if(!text.equals("")) {
                input_price.setText(text);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tempImageUri = getArguments().getParcelable(ARG_TEMP_IMAGE);
            mFragmentTag = getArguments().getString(ARG_FRAGMENT_TAG);
            mImageUri = getArguments().getParcelable(ARG_IMAGE_URI);
            price = getArguments().getString(ARG_IMAGE_PRICE);
            comment = getArguments().getString(ARG_IMAGE_COMMENT);
            mEditType = getArguments().getInt(ARG_TYPE);
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

    private void grabImage(ImageView imageView){
        Log.d("TAG","grabImageStart");
        Glide.with(this)
                .load(tempImageUri)
                .into(imageView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_redact_image, container, false);
        image_preview = v.findViewById(R.id.image_preview);
        input_comment = v.findViewById(R.id.input_comment);
        input_price = v.findViewById(R.id.input_price);
        if(mEditType == TYPE_EDIT){
            setupIInk(v);
            input_comment.setText(comment);
            input_price.setText(price);
        }
        else{
            String text = "Hey, I want to buy it. It costs "+price+". What do you think?";
            input_price.setVisibility(View.GONE);
            v.findViewById(R.id.redact_buttons).setVisibility(View.GONE);
            v.findViewById(R.id.editor_view).setVisibility(View.GONE);
            input_comment.setText(text);
        }

        image_preview.post(new Runnable() {
            @Override
            public void run() {
                if(tempImageUri != null) {
                    grabImage(image_preview);
                }else{
                    Log.d("TAG","Uri: "+mImageUri.getPath());
                    Glide.with(getActivity())
                            .load(mImageUri.getPath())
                            .listener(requestListener)
                            .placeholder(android.R.color.holo_green_light)
                            .error(android.R.color.holo_red_light)
                            .fallback(android.R.color.holo_orange_light)
                            .into(image_preview);
                }
            }
        });


        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button_undo:
                editorView.getEditor().undo();
                break;
            case R.id.button_redo:
                editorView.getEditor().redo();
                break;
            case R.id.button_clear:
                editorView.getEditor().clear();
                input_price.setText("");
                break;
            default:
                Log.e("TAG", "Failed to handle click event");
                break;
        }
    }

    public void onButtonPressed(String uri, String price, String comment) {
        if (mListener != null) {
            if(mEditType == TYPE_EDIT) {
                mListener.onRedactFragmentInteraction(uri, price, comment);
                getActivity().getFragmentManager().popBackStack();
            }
            else{
                ItemInformationFragment.createShareIntent(getActivity(),uri,comment);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        mFragmentTag = getArguments().getString(ARG_FRAGMENT_TAG);
        Log.d("TAG","onAttach redact "+mFragmentTag);
        super.onAttach(context);

        Fragment fragment = ((Activity) context).getFragmentManager().findFragmentByTag(mFragmentTag);
        if (fragment instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) fragment;
        } else {
            throw new RuntimeException(fragment.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Fragment fragment = getFragmentManager().findFragmentByTag(mFragmentTag);
        try {
            this.mListener = (RedactImageFragment.OnFragmentInteractionListener)fragment;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        if(mEditType == TYPE_EDIT) {
            editorView.setOnTouchListener(null);
            editorView.close();
            if (contentPart != null) {
                contentPart.close();
                contentPart = null;
            }
            if (contentPackage != null) {
                contentPackage.close();
                contentPackage = null;
            }
            engine = null;
        }

        super.onDestroy();
    }

    public interface OnFragmentInteractionListener {
        void onRedactFragmentInteraction(String uri,String price, String comment);
        void onShareFragmentInteraction(String uri,String comment);
    }


    private void submit_wish_item(){
        comment = input_comment.getText().toString();

        if(mEditType == TYPE_EDIT){
            price = input_price.getText().toString();
        }
        onButtonPressed(mImageUri.getPath(), price,comment);
    }







    private void setupIInk(View v){
        Log.d("TAG","setupIInk");
        engine = IInkApplication.getEngine();

        Configuration conf = engine.getConfiguration();
        String confDir = "zip://" + getActivity().getPackageCodePath() + "!/assets/conf";
        conf.setStringArray("configuration-manager.search-path", new String[] { confDir });
        String tempDir = getActivity().getFilesDir().getPath() + File.separator + "tmp";
        conf.setString("content-package.temp-folder", tempDir);

        editorView = v.findViewById(R.id.editor_view);
        editorView.setEngine(engine);

        final Editor editor = editorView.getEditor();
        editor.addListener(new IEditorListener() {
            @Override
            public void partChanging(Editor editor, ContentPart contentPart, ContentPart contentPart1) {
            }

            @Override
            public void partChanged(Editor editor) {
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void contentChanged(Editor editor, String[] strings) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        convertPrice();
                    }
                });
                getActivity().invalidateOptionsMenu();
                invalidateIconButtons();
            }

            @Override
            public void onError(Editor editor, String s, String s1) {
                Log.e("TAG","Failed to edit block \"" + s + "\"" + s1);
            }
        });

        editorView.setInputController(new InputController(getActivity(),editorView));
        editorView.setInputMode(InputController.INPUT_MODE_FORCE_PEN);

        String packageName = "File1.iink";
        File file = new File(getActivity().getFilesDir(), packageName);
        try{
            contentPackage = engine.createPackage(file);
            contentPart = contentPackage.createPart("Text");

        }catch(IOException e){
            Log.e("TAG","Failed to open packege "+packageName+" "+e);
        }
        catch (IllegalArgumentException e)
        {
            Log.e("TAG", "Failed to open package \"" + packageName + "\"", e);
        }

        editorView.post(new Runnable() {
            @Override
            public void run() {
                editorView.getRenderer().setViewOffset(0,0);
                editorView.getRenderer().setViewScale(1);
                editorView.setVisibility(View.VISIBLE);
                editor.setPart(contentPart);
            }
        });
        v.findViewById(R.id.button_undo).setOnClickListener(this);
        v.findViewById(R.id.button_redo).setOnClickListener(this);
        v.findViewById(R.id.button_clear).setOnClickListener(this);
        Log.d("TAG","setupIInkEnd");
    }

    private void invalidateIconButtons(){
        Editor editor = editorView.getEditor();
        final boolean canUndo = editor.canUndo();
        final boolean canRedo = editor.canRedo();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageButton imageButtonUndo = (ImageButton)getActivity().findViewById(R.id.button_undo);
                imageButtonUndo.setEnabled(canUndo);
                ImageButton imageButtonRedo = (ImageButton)getActivity().findViewById(R.id.button_redo);
                imageButtonRedo.setEnabled(canRedo);
                ImageButton imageButtonClear = (ImageButton)getActivity().findViewById(R.id.button_clear);
                imageButtonClear.setEnabled(contentPart != null);
            }
        });
    }
}
