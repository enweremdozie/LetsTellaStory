package com.letstellastory.android.letstellastory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

public class Story_Grid extends AppCompatActivity {
    private StaggeredGridLayoutManager _sGridLayoutManager;
    String story;
    String genre;
    DBHelper myDb;
    TextView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_grid);
        myDb = new DBHelper(this);

        back = (TextView) findViewById(R.id.grid_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Story_Grid.this, MainActivity.class);
                startActivity(intent);
            }
        });

        /*RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        _sGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(_sGridLayoutManager);

        List<ItemObject> sList = getListItemData();

        StoryRecycler rcAdapter = new StoryRecycler(Story_Grid.this, sList);
        recyclerView.setAdapter(rcAdapter);*/
    }

    /*private List<ItemObject> getListItemData() {
        Intent intent = getIntent();
        story = intent.getExtras().getString("title");
        genre = intent.getExtras().getString("genre");
        List<ItemObject> listViewItems = new ArrayList<ItemObject>();
        listViewItems.add(new ItemObject(story, genre));
        /*listViewItems.add(new ItemObject("Pride and Prejudice", "Horror"));
        listViewItems.add(new ItemObject("One Hundred Years of Solitude", "Romance"));
        listViewItems.add(new ItemObject("The Book Thief", "Romance"));
        listViewItems.add(new ItemObject("The Hunger Games", "Horror"));
        listViewItems.add(new ItemObject("The Hitchhiker's Guide to the Galaxy", "Drama"));
        listViewItems.add(new ItemObject("The Theory Of Everything", "Romance"));

        return listViewItems;
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            // do something on back.
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return true;
    }
}
