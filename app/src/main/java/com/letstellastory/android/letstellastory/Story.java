package com.letstellastory.android.letstellastory;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Story extends AppCompatActivity {
    TextView post, show;
    DBHelper db;
    EditText storyED;
    String ActTitle, genre, story, storyTeller;
    long storyTime;
    private FirebaseListAdapter<newStory> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        db = new DBHelper(this);

        post = (TextView) findViewById(R.id.post);
        //show = (TextView) findViewById(R.id.showAll);
        Intent intent = getIntent();
        ActTitle = intent.getExtras().getString("title");
        genre = intent.getExtras().getString("genre");
        //Toast.makeText(Story.this, genre, Toast.LENGTH_LONG).show();
        setTitle(ActTitle);
        AddData();
        //viewAll();

    }

    private void displayStory() {
        ListView storyList = (ListView)findViewById(R.id.stories);
        adapter = new FirebaseListAdapter<newStory>(this, newStory.class, R.layout.list_item, FirebaseDatabase.getInstance().getReference())
        {
            @Override
            protected void populateView(View v, newStory model, int position) {
                TextView story;
                story = (TextView) v.findViewById(R.id.userStory);

                story.setText(model.getStory());
            }
        };
        storyList.setAdapter(adapter);
    }


    /*public Story(String story, String storyTeller) {
        this.story = story;
        this.storyTeller = storyTeller;

        storyTime = new Date().getTime();
    }

    public Story() {


    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getStoryTeller() {
        return storyTeller;
    }

    public void setStoryTeller(String storyTeller) {
        this.storyTeller = storyTeller;
    }

    public long getStoryTime() {
        return storyTime;
    }

    public void setStoryTime(long storyTime) {
        this.storyTime = storyTime;
    }*/

    public void AddData() {

        post.setOnClickListener(new View.OnClickListener() {

            //story = ()
            @Override
            public void onClick(View v) {

                EditText input = (EditText) findViewById(R.id.storyEdit);
                FirebaseDatabase.getInstance().getReference().push().setValue(new newStory(input.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                input.setText("");


                storyED = (EditText) findViewById(R.id.storyEdit);
                story = storyED.getText().toString();
                boolean isInserted = db.insertData(ActTitle, genre, story);

                if (isInserted == true) {
                    Toast.makeText(Story.this, "Data Inserted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Story.this, "Data not Inserted", Toast.LENGTH_LONG).show();

                }
                //displayStory();
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

