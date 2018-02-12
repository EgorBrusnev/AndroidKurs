package com.itrans.kurs.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.itrans.kurs.R;


public class WishAddFragment extends DialogFragment {
    private final static String ARG_TYPE = "type";
    private final static int TYPE_ADD = 0;
    private final static int TYPE_EDIT = 1;

    private int mType;

    private OnFragmentInteractionListener mListener;
    Button wish_button;
    EditText wish_input;

    public WishAddFragment() {
        // Required empty public constructor
    }

    public static WishAddFragment newInstance(int type) {
        WishAddFragment fragment = new WishAddFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE,type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mType = getArguments().getInt(ARG_TYPE);
            Log.d("TAG","mType="+mType);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("What do you wish?");
        View view = inflater.inflate(R.layout.fragment_add_wish,null);
        wish_button = view.findViewById(R.id.wish_button);
        wish_input = view.findViewById(R.id.wish_input);
        wish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String wish = wish_input.getText().toString();
                dismiss();
                onButtonPressed(wish);
            }
        });
        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String text) {
        Log.d("TAG","Type "+mType);
        if (mListener != null) {
            switch (mType){
                case TYPE_ADD:
                    mListener.onAddFragmentInteraction(text);
                    break;
                case TYPE_EDIT:
                    mListener.onEditFragmentInteraction(text);
                    break;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Fragment fragment = ((Activity) context).getFragmentManager().findFragmentByTag("main_list_fragment");
        if (fragment instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) fragment;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MainListFragment fragment = (MainListFragment) getFragmentManager().findFragmentByTag("main_list_fragment");
        try {
            this.mListener = (OnFragmentInteractionListener)fragment;
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

    public interface OnFragmentInteractionListener {
        void onAddFragmentInteraction(String text);
        void onEditFragmentInteraction(String text);
    }
}
