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
        String user,password,story,genre,currentUser;


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle mArgs = getArguments();
            user = mArgs.getString("user");
            password = mArgs.getString("password");
            story = mArgs.getString("story");
            genre = mArgs.getString("genre");
            currentUser = mArgs.getString("currentUser");
            Log.d("CURRENTUSER", "current user in Dialog: " + currentUser);

           // Log.d("CREATION", "in DialogFrag password is " + password);
                // Use the Builder class for convenient dialog construction
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Start a story");
                builder.setMessage("Start your story with a paragraph, pass it on and watch your paragraph become hundreds of pages." +
                        " As the creator of a story you have the ability to \n-Keep track of what is added to your story" + "\n-End the story." +
                                "\nCreating a story gives every writer in your story including you the ability to delete any paragraph they do not want in your story on each writers end"
                         );


                builder.setPositiveButton("GO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent intent = new Intent(getActivity(), StartAStory.class);
                        Log.d("LOADSTORY", "password in Dialog: " + password);
                        intent.putExtra("story", story);
                        intent.putExtra("genre", genre);
                        intent.putExtra("user", user);
                        intent.putExtra("password", password);
                        intent.putExtra("currentUser", currentUser);
                        startActivity(intent);

                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                return builder.create();
            }


}
