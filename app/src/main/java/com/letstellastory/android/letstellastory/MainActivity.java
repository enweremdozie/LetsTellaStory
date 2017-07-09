package com.letstellastory.android.letstellastory;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    int fragPos;
    private static int SIGN_IN_REQUEST_CODE = 1;
    //private FirebaseListAdapter<Story> adapter;
    LinearLayout activity_main;
    TextView post;



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Snackbar.make(activity_main, "Successfully signed in", Snackbar.LENGTH_SHORT).show();
                //displayStory();
            }
            else {
                Snackbar.make(activity_main, "We couldnt sign you in. Please try again later", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity_main = (LinearLayout) findViewById(R.id.activity_main);
        /*post = (TextView) findViewById(R.id.post);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = (EditText) findViewById(R.id.storyEdit);
                FirebaseDatabase.getInstance().getReference().push().setValue(new Story(input.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                input.setText("");
            }
        });*/
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

        //Check if not signed-in then navigate to sign in page
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        }
        else
        {
            Snackbar.make(activity_main, "Welcome " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT).show();
        }


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

        else if(item.getItemId() == R.id.menu_stories){
            Intent intent = new Intent(this, theStories.class);
            startActivity(intent);
        }

        else if(item.getItemId() == R.id.menu_sign_out){
          AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                  Snackbar.make(activity_main, "You have been signed out", Snackbar.LENGTH_SHORT).show();
                  finish();
              }
          });
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
