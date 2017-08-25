package com.letstellastory.android.letstellastory;

import android.app.DialogFragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

public class theStories extends AppCompatActivity {
    int fragPos;
    int whichFrag = 0;
    TextView back;
    static String story, genre;
    Invited_Stories_Fragment isFrag;
    static String user;
    static String password;
    String currentUser;

    @Override
    protected void onRestart() {
        super.onRestart();
        QBChatService.getInstance().setReconnectionAllowed(true);

        createSessionForStory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        QBSettings.getInstance().setAccountKey("UYy6wj-dzPJ4ePBZdMJM");

        QBSettings.getInstance().init(getApplicationContext(),"60149","NnE9q3LKjvKz6-e","hcWYgEvZmpcn5s8");

        QBChatService.getInstance().setReconnectionAllowed(true);

        createSessionForStory();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logOut();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_stories);

        QBSettings.getInstance().setAccountKey("UYy6wj-dzPJ4ePBZdMJM");
        QBSettings.getInstance().init(getApplicationContext(),"60149","NnE9q3LKjvKz6-e","hcWYgEvZmpcn5s8");


        QBChatService.setDefaultAutoSendPresenceInterval(600);
        QBChatService.getInstance().setReconnectionAllowed(true);

        setTitle("STORIES");
        centerTitle();
        createSessionForStory();


        // Find the view pager that will allow the user to swipe between fragments
        final ViewPager viewPager = (ViewPager) findViewById(R.id.story_viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        SimpleStoryPagerAdapter adapter = new SimpleStoryPagerAdapter(getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.story_tab_layout);
        tabLayout.setupWithViewPager(viewPager);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 1){
                whichFrag = 1;
            }

            else if(position == 0){
                whichFrag = 0;

            }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        Intent intent = getIntent();

        DBHelper mystories = new DBHelper(theStories.this);
        mystories.insertData_my_stories(user, password);

        Intent intent2 = getIntent();
        story = intent2.getExtras().getString("title");
        genre = intent2.getExtras().getString("genre");
        currentUser = intent2.getExtras().getString("currentUser");
        user = intent2.getExtras().getString("user");
        password = intent2.getExtras().getString("password");

        receivePush();

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

         if(item.getItemId() == R.id.profile){
            showUserProfile();
        }
        else if(item.getItemId() == R.id.start){
            Bundle args = new Bundle();
            args.putString("user", user);
            args.putString("password", password);
            args.putString("story", story);
            args.putString("genre", genre);
             args.putString("currentUser", currentUser);
            DialogFragment dialog = new CreateDialogFragment();
            dialog.setArguments(args);
            dialog.show(getFragmentManager(), "CreateDialogFragment.tag");
        }

        else if(item.getItemId() == R.id.start_local){
             Intent intent = new Intent(theStories.this, StartALocalStory.class);
             intent.putExtra("currentUser", currentUser);
             intent.putExtra("user", user);
             intent.putExtra("password", password);
             startActivity(intent);
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
                        Toast.makeText(theStories.this, "Signed out", Toast.LENGTH_SHORT).show();
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
        intent.putExtra("currentUser", currentUser);
        intent.putExtra("user", user);
        intent.putExtra("password", password);
        startActivity(intent);
    }

    private void createSessionForStory(){

        final QBUser qbUser = new QBUser(user, password);

        QBAuth.createSession(qbUser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

    }

    private void centerTitle() {
        ArrayList<View> textViews = new ArrayList<>();

        getWindow().getDecorView().findViewsWithText(textViews, getTitle(), View.FIND_VIEWS_WITH_TEXT);

        if(textViews.size() > 0) {
            AppCompatTextView appCompatTextView = null;
            if(textViews.size() == 1) {
                appCompatTextView = (AppCompatTextView) textViews.get(0);
            } else {
                for(View v : textViews) {
                    if(v.getParent() instanceof Toolbar) {
                        appCompatTextView = (AppCompatTextView) v;
                        break;
                    }
                }
            }

            if(appCompatTextView != null) {
                ViewGroup.LayoutParams params = appCompatTextView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                appCompatTextView.setLayoutParams(params);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    appCompatTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(2).setVisible(false);

        if(whichFrag == 1 && QBChatService.getInstance().getUser().getId().toString().equals("31009125")){
            menu.getItem(1).setVisible(true);
        }

        else {
            menu.getItem(1).setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    public void receivePush(){
        Intent intent1  = new Intent(this, MainActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;//my request code
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent1, PendingIntent.FLAG_ONE_SHOT);


        BroadcastReceiver pushBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra("message");
                String from = intent.getStringExtra("from");

                Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                //Build notification
                long[] pattern = {250,250,250,250};

                NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(getBaseContext())
                        .setSmallIcon(R.mipmap.letstellastory_icon)
                        .setContentTitle("New story")
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setSound(sound)
                        .setLights(Color.WHITE, 500, 500)
                        .setVibrate(pattern);

                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0,noBuilder.build());//0 = ID of notification
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(pushBroadcastReceiver,
                new IntentFilter("new-push-event"));

    }

    public void nostories(){
        AlertDialog.Builder builder = new AlertDialog.Builder(theStories.this);
        builder.setTitle("No local stories");
        builder.setMessage("As there are not enough people signed up to \"Lets tell a story\" there will be only one story join but please feel free to start your own story at any time, sorry for the inconvenience.");


        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                // You don't have to do anything here if you just want it dismissed when clicked
            }
        });
        builder.show();
    }

}


