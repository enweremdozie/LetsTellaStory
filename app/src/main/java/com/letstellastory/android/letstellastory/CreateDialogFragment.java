package com.letstellastory.android.letstellastory;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by dozie on 2017-06-25.
 */

public class CreateDialogFragment extends DialogFragment {
        String user,password,story,genre;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle mArgs = getArguments();
            user = mArgs.getString("user");
            password = mArgs.getString("password");
            story = mArgs.getString("story");
            genre = mArgs.getString("genre");

            Log.d("CREATION", "in DialogFrag password is " + password);
                // Use the Builder class for convenient dialog construction
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Start a story");
                builder.setMessage("Start your own story, pass it on to a friend and watch your paragraph become hundreds of pages." +
                        " As the administrator of a story you have the ability to \n-Keep track of what is added to your story \n-Delete any paragraph that you do not want in your story\n" +
                        "-Pass a start to any friend you want to start the story \n-End the story");


                builder.setPositiveButton("GO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Context context = getContext();

                        Intent intent = new Intent(getActivity(), StartAStory.class);
                        intent.putExtra("user", user);
                        intent.putExtra("password", password);
                        intent.putExtra("story", story);
                        intent.putExtra("genre", genre);
                        startActivity(intent);
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
