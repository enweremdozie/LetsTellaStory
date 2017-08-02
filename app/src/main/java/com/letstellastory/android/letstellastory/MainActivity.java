package com.letstellastory.android.letstellastory;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.letstellastory.android.letstellastory.Holder.QBUsersHolder;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.services.QBPushManager;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

//import com.quickblox.messages.services.QBPushManager;

public class MainActivity extends AppCompatActivity {


    static final String APP_ID = "60149";
    static final String AUTH_KEY = "NnE9q3LKjvKz6-e";
    static final String AUTH_SECRET = "hcWYgEvZmpcn5s8";
    static final String ACCOUNT_KEY = "UYy6wj-dzPJ4ePBZdMJM";
    static final int REQUEST_CODE = 1000;

    Button btnLogin, btnSignUp;
    EditText edtUser, edtPassword;
    DBHelper helper;
    SQLiteDatabase sqLiteDatabase;
    String id, user, password;

    //
    //private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onRestart() {
        super.onRestart();
        createSessionForStory();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.w("MainActivity", "onPause");
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }


    @Override
    protected void onResume() {
        super.onResume();
        createSessionForStory();

       /* Log.w("MainActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //helper.onCreate(sqLiteDatabase);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
        QBSettings.getInstance().init(getApplicationContext(), APP_ID,AUTH_KEY,AUTH_SECRET );

        /*QBSubscription qbSubscription = new QBSubscription();
        qbSubscription.setNotificationChannel(QBNotificationChannel.GCM);
        String androidID = com.quickblox.core.helper.Utils.generateDeviceId(getBaseContext());
        qbSubscription.setDeviceUdid(androidID);
        qbSubscription.setRegistrationID("30816491");
        qbSubscription.setEnvironment(QBEnvironment.DEVELOPMENT);*/

        QBPushManager.getInstance().addListener(new QBPushManager.QBSubscribeListener() {
            @Override
            public void onSubscriptionCreated() {
                Log.d("PUSHNOT", "onSubscriptionCreated");
            }

            @Override
            public void onSubscriptionError(final Exception e, int resultCode) {
                Log.d("PUSHNOT", "onSubscriptionError" + e);
                if (resultCode >= 0) {
                    Log.d("PUSHNOT", "Google play service exception" + resultCode);
                }
                Log.d("PUSHNOT", "onSubscriptionError " + e.getMessage());
            }

            @Override
            public void onSubscriptionDeleted(boolean b) {

            }
        });



        /*BroadcastReceiver pushBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra("message");
                String from = intent.getStringExtra("from");
                Log.i("PUSHNOT", "Receiving message: " + message + ", from " + from);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(pushBroadcastReceiver,
                new IntentFilter("new-push-event"));*/


        QBSettings.getInstance().setAutoCreateSession(true);


        centerTitle();

        Log.d("PUSHNOT", "push not status: " + QBSettings.getInstance().isEnablePushNotification());

        //QBSettings.getInstance().setEnablePushNotification(true);
        QBChatService.ConfigurationBuilder builder = new QBChatService.ConfigurationBuilder();
        builder.setAutojoinEnabled(true);
        QBChatService.setConfigurationBuilder(builder);



        QBSettings.getInstance().setEnablePushNotification(true);
        requestRunTimePermission();
        //getListItemData();
        //initializeFramework();

        btnLogin = (Button) findViewById(R.id.main_btnLogin);
        btnSignUp = (Button) findViewById(R.id.main_btnSignUp);

        edtPassword = (EditText) findViewById(R.id.main_editPassword);
        edtUser = (EditText) findViewById(R.id.main_editLogin);
        getListItemData();








        /*mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Check type of intent filter
                if(intent.getAction().endsWith(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                    String token = intent.getStringExtra("token");
                    Toast.makeText(getApplicationContext(), "GCM token: " + token, Toast.LENGTH_LONG).show();
                }
                else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){
                    //Registration error
                    Toast.makeText(getApplicationContext(), "GCM registration error!!!", Toast.LENGTH_LONG).show();
                }
                else{

                }
            }
        };


        //check status of google play services in device
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if(ConnectionResult.SUCCESS != resultCode){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                Toast.makeText(getApplicationContext(), "Google Play Service is not installed/enabled on this device", Toast.LENGTH_LONG).show();
                //so notification
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
            }
            else{
                Toast.makeText(getApplicationContext(), "This device does not support Google Play Service!", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Intent intent1 = new Intent(this, GCMRegistrationIntentService.class);
            startService(intent1);
        }*/


        btnSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String user = edtUser.getText().toString();
                final String password = edtPassword.getText().toString();

                QBUser qbUser = new QBUser(user, password);

                QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        Toast.makeText(getBaseContext(), "Login successful", Toast.LENGTH_SHORT).show();


                        /*Intent passInfo = new Intent (MainActivity.this, Story.class);
                        passInfo.putExtra("user", user);
                        passInfo.putExtra("password", password);*/

                        Intent intent = new Intent(MainActivity.this, theStories.class);
                        intent.putExtra("user", user);
                        intent.putExtra("password", password);

                        startActivity(intent);
                        //finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(getBaseContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        createSessionForStory();

    }
    private void requestRunTimePermission() {
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case REQUEST_CODE:
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getBaseContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getBaseContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    private void getListItemData() {
        DBHelper helper = new DBHelper(MainActivity.this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = helper.getMyStoriesInformations(db);


        //user = cursor.getString(cursor.getColumnIndex(helper.COL_TITLE));

        //Toast.makeText(this, "user" , Toast.LENGTH_SHORT).show();
        /*edtUser.setText("dozie");
        edtPassword.setText("evans909");*/
        while (cursor.moveToNext()) {
            id = cursor.getString(cursor.getColumnIndex(helper.COL_ID));
            user = cursor.getString(cursor.getColumnIndex(helper.COL_TITLE));
            password = cursor.getString(cursor.getColumnIndex(helper.COL_GENRE));
            //Toast.makeText(this, "user "+ user, Toast.LENGTH_LONG).show();
            if(user != null && password != null){
                edtUser.setText(user);
                edtUser.setSelection(user.length());
                edtPassword.setText(password);
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            moveTaskToBack(true);
            MainActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /*private void initializeFramework() {
        QBSettings.getInstance().init(getApplicationContext(),APP_ID,AUTH_KEY,AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
    }*/

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

    private void createSessionForStory(){

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
        //Log.d("CREATION", "in story fragment password is " + password);
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


    @Override
    protected void onStop() {
        super.onStop();

        DBHelper helper = new DBHelper(MainActivity.this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = helper.getMyStoriesInformations(db);
        try {
            // get data from cursor
        } catch (Exception e) {
            // exception handling
        } finally {
            if(cursor != null){
                cursor.close();
            }
        }
    }

}
