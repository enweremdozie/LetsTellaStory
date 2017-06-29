package com.letstellastory.android.letstellastory;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

/**
 * Created by dozie on 2017-06-25.
 */

public class CreateDialogFragment extends DialogFragment {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

                // Use the Builder class for convenient dialog construction
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Start a story");
                builder.setMessage("Start your own story, pass it on to a friend and watch your paragraph become hundreds of pages." +
                        " As the administrator of a story you have the ability to \n-Keep track of what is added to your story \n-Delete any paragraph that you do not want in your story\n" +
                        "-Pass a start to any friend you want to start the story \n-End the story");


                builder.setPositiveButton("GO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Context context = getContext();
                        Intent myIntent = new Intent(getActivity(), StartAStory.class);
                        startActivity(myIntent);
                        // You don't have to do anything here if you just want it dismissed when clicked
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // You don't have to do anything here if you just want it dismissed when clicked
                    }
                });

                // Create the AlertDialog object and return it
                return builder.create();
            }


}
