package com.letstellastory.android.letstellastory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class Story extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        Intent intent = getIntent();
        String ActTitle = intent.getExtras().getString("title");
        String genre = intent.getExtras().getString("genre");
        Toast.makeText(Story.this, genre, Toast.LENGTH_LONG).show();
        setTitle(ActTitle);
    }
}
