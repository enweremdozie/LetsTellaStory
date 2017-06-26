package com.letstellastory.android.letstellastory;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    SimpleFragmentPagerAdaptor frag;
    Object object;
    int fragPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        SimpleFragmentPagerAdaptor adapter = new SimpleFragmentPagerAdaptor(getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        /*Intent intent = new Intent(this, Drama.class);
        startActivity(intent);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        fragPos = getFragPos();
        if(item.getItemId() == R.id.start){
            DialogFragment dialog = new CreateDialogFragment();
            dialog.show(getFragmentManager(), "CreateDialogFragment.tag");
            //Toast.makeText(this, "start", Toast.LENGTH_LONG).show();
            Log.d("CREATION", "creating in drama");
        }
        return super.onOptionsItemSelected(item);
    }

    public void setFragPos(int newFrag){

        fragPos = newFrag;
    }

    public int getFragPos() {
        return fragPos;
    }
}
