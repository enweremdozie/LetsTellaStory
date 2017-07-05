package com.letstellastory.android.letstellastory;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Story extends AppCompatActivity {
    TextView post, show;
    DBHelper db;
    EditText storyED;
    String ActTitle, genre, story;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        db = new DBHelper(this);

        post = (TextView) findViewById(R.id.post);
        show = (TextView) findViewById(R.id.showAll);
        Intent intent = getIntent();
        ActTitle = intent.getExtras().getString("title");
        genre = intent.getExtras().getString("genre");
        Toast.makeText(Story.this, genre, Toast.LENGTH_LONG).show();
        setTitle(ActTitle);
        AddData();
        viewAll();
    }

    public void AddData() {

        post.setOnClickListener(new View.OnClickListener() {

            //story = ()
            @Override
            public void onClick(View v) {
                storyED = (EditText) findViewById(R.id.storyEdit);
                story = storyED.getText().toString();
                boolean isInserted = db.insertData(ActTitle, genre, story);

                if (isInserted == true) {
                    Toast.makeText(Story.this, "Data Inserted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Story.this, "Data not Inserted", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    public void viewAll() {

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor res = db.getAllData();
                if (res.getCount() == 0) {
                    //if nothing is the database
                    showMessage("Error", "No data found");
                    return;
                }
                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()) {
                    buffer.append("Id : " + res.getString(0) + "\n");
                    buffer.append("Title : " + res.getString(1) + "\n");
                    buffer.append("Genre : " + res.getString(2) + "\n");
                    buffer.append("Story : " + res.getString(3) + "\n\n");
                }
                showMessage("Data", buffer.toString());
            }
        });
    }


    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }
}
   /* public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();

    }*/

