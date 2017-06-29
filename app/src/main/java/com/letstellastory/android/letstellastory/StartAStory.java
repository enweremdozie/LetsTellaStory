package com.letstellastory.android.letstellastory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
    RadioButton btnDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_astory);
        cancel = (TextView) findViewById(R.id.cancel);
        okay = (TextView) findViewById(R.id.ok);

        genre = (RadioGroup) findViewById(R.id.radGroup);



        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storyName = (EditText) findViewById(R.id.storyName);
                et = storyName.getText().toString();
                int selectedId = genre.getCheckedRadioButtonId();

                btnDisplay = (RadioButton) findViewById(selectedId);
                if(et.length() > 0) {
                    Intent intent = new Intent(v.getContext(), Story.class);
                    intent.putExtra("title", et);
                    intent.putExtra("genre", btnDisplay.getText());
                    //Toast.makeText(StartAStory.this, radioButton.getText(), Toast.LENGTH_LONG).show();
                    startActivity(intent);
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
