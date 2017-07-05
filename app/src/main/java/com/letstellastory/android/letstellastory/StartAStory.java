package com.letstellastory.android.letstellastory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class StartAStory extends AppCompatActivity {
    TextView cancel;
    TextView okay;
    EditText storyName;
    String et;
    RadioGroup genre;
    RadioGroup length;
    RadioButton genreDisplay;
    RadioButton storyLength;
    CheckBox pass;
    String passState;
    TextView show;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_astory);
        cancel = (TextView) findViewById(R.id.cancel);
        okay = (TextView) findViewById(R.id.ok);

        genre = (RadioGroup) findViewById(R.id.radGroup);
        length = (RadioGroup) findViewById(R.id.length);
        pass = (CheckBox) findViewById(R.id.PassaStart);




        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });




        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                storyName = (EditText) findViewById(R.id.storyName);
                et = storyName.getText().toString();
                int selectedId = genre.getCheckedRadioButtonId();
                int lengthId =  length.getCheckedRadioButtonId();

                genreDisplay = (RadioButton) findViewById(selectedId);
                storyLength = (RadioButton) findViewById(lengthId);
                if(pass.isChecked()){
                    passState = "yes";
                }
                else{
                    passState = "no";
                }

                if(et.length() > 0) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(StartAStory.this);
                    builder.setTitle("Story snippet");
                    builder.setMessage("Story name: " + et + "\n" +
                                        "Genre: " + genreDisplay.getText().toString() + "\n" +
                                          "Story length: " + storyLength.getText().toString() + "\n" +
                                            "Pass a start: " + passState);


                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(v.getContext(), Story.class);
                            //Intent intent = new Intent(v.getContext(), Story_Grid.class);                         //over here
                            intent.putExtra("title", et);
                            intent.putExtra("genre", genreDisplay.getText());
                            //Toast.makeText(StartAStory.this, radioButton.getText(), Toast.LENGTH_LONG).show();
                            startActivity(intent);
                            // You don't have to do anything here if you just want it dismissed when clicked
                        }
                    });

                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            // You don't have to do anything here if you just want it dismissed when clicked
                        }
                    });
                    builder.show();

                }

                else{

                    //Toast.makeText(StartAStory.this, "working", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(StartAStory.this);
                    builder.setTitle("");
                    builder.setMessage("Please name your story");


                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            // You don't have to do anything here if you just want it dismissed when clicked
                        }
                    });
                    builder.show();
                }
            }
        });
    }

}
