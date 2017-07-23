package com.letstellastory.android.letstellastory;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.letstellastory.android.letstellastory.Holder.QBUsersHolder;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

public class theStories extends AppCompatActivity {
    int fragPos;
    TextView back;
    static String story, genre;
    Invited_Stories_Fragment isFrag;
    static String user;
    static String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_stories);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
       /* back = (TextView) findViewById(R.id.story_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(theStories.this, MainActivity.class);
                startActivity(intent);
            }
        });*/
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

        user = intent.getExtras().getString("user");
        password = intent.getExtras().getString("password");

        DBHelper mystories = new DBHelper(theStories.this);
            mystories.insertData_my_stories(user, password);

        //isFrag.setaStory(story);
        //isFrag.setGenre(genre);
        //Toast.makeText(theStories.this, story, Toast.LENGTH_LONG).show();

        //Log.d("CREATION", "in the stories password is " + password);
        if(story == null) {
            //Toast.makeText(theStories.this, story, Toast.LENGTH_LONG).show();
            createSessionForStory();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    public void setFragPos(int newFrag){

        fragPos = newFrag;
    }


    public int getFragPos() {
        return fragPos;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        fragPos = getFragPos();

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        if(item.getItemId() == R.id.profile){
            showUserProfile();
        }
        if(item.getItemId() == R.id.start){
            Bundle args = new Bundle();
            args.putString("user", user);
            args.putString("password", password);
            args.putString("story", story);
            args.putString("genre", genre);

            DialogFragment dialog = new CreateDialogFragment();
            dialog.setArguments(args);
            dialog.show(getFragmentManager(), "CreateDialogFragment.tag");
            //Toast.makeText(this, "start", Toast.LENGTH_LONG).show();
            //Log.d("CREATION", "creating in drama");
        }

        else if(item.getItemId() == R.id.join){

        }

        else if(item.getItemId() == R.id.menu_sign_out){
            logOut();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        QBUsers.signOut().performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                QBChatService.getInstance().logout(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        Toast.makeText(theStories.this, "Logged out", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(theStories.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    private void showUserProfile() {
        Intent intent = new Intent(theStories.this, UserProfile.class);
        startActivity(intent);
    }

    private void createSessionForStory(){
        /*final ProgressDialog mDialog = new ProgressDialog(theStories.this);
        mDialog.setMessage("Please wait...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();*/

        //String user = theStories.user;
        //String password = theStories.password;

        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                QBUsersHolder.getInstance().putUsers(qbUsers);
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });


        final QBUser qbUser = new QBUser(user, password);
        Log.d("CREATION", "in story fragment password is " + password);
        QBAuth.createSession(qbUser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                qbUser.setId(qbSession.getUserId());
                try {
                    qbUser.setPassword(BaseService.getBaseService().getToken());
                } catch (BaseServiceException e) {
                    e.printStackTrace();
                }

                QBChatService.getInstance().login(qbUser, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        //mDialog.dismiss();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("ERROR",""+e.getMessage());
                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }
}


