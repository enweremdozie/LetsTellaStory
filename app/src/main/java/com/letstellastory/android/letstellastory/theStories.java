package com.letstellastory.android.letstellastory;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class theStories extends AppCompatActivity {
    int fragPos;
    TextView back;
    String story, genre;
    Invited_Stories_Fragment isFrag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_stories);

        back = (TextView) findViewById(R.id.story_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(theStories.this, MainActivity.class);
                startActivity(intent);
            }
        });
        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = (ViewPager) findViewById(R.id.story_viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        SimpleStoryPagerAdapter adapter = new SimpleStoryPagerAdapter(getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.story_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        Intent intent = getIntent();
        story = intent.getExtras().getString("title");
        genre = intent.getExtras().getString("genre");
        //isFrag.setaStory(story);
        //isFrag.setGenre(genre);
    }

    public void setFragPos(int newFrag){

        fragPos = newFrag;
    }

    public int getFragPos() {
        return fragPos;
    }

}
