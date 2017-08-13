package com.letstellastory.android.letstellastory;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * Created by dozie on 2017-07-08.
 */

public class Local_Stories_Fragment extends Fragment {

    GridView gridview;
    String story, genre;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_drama_, container, false);


            Log.d("ENTERSLOCAL", "enters local");

        //nostories();



        //Toast.makeText(getActivity(), "Enters local frag", Toast.LENGTH_SHORT).show();
        return view;
    }


}
