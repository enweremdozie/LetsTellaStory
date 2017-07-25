package com.letstellastory.android.letstellastory;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    static final String APP_ID = "60149";
    static final String AUTH_KEY = "NnE9q3LKjvKz6-e";
    static final String AUTH_SECRET = "hcWYgEvZmpcn5s8";
    static final String ACCOUNT_KEY = "UYy6wj-dzPJ4ePBZdMJM";

    Button btnLogin, btnSignUp;
    EditText edtUser, edtPassword;
    DBHelper helper;
    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //helper.onCreate(sqLiteDatabase);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        QBChatService.ConfigurationBuilder builder = new QBChatService.ConfigurationBuilder();
        builder.setAutojoinEnabled(true);
        QBChatService.setConfigurationBuilder(builder);

        centerTitle();
        //getListItemData();
        initializeFramework();

        btnLogin = (Button) findViewById(R.id.main_btnLogin);
        btnSignUp = (Button) findViewById(R.id.main_btnSignUp);

        edtPassword = (EditText)findViewById(R.id.main_editPassword);
        edtUser = (EditText)findViewById(R.id.main_editLogin);
        getListItemData();

        btnSignUp.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SignUpActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                final String user = edtUser.getText().toString();
                final String password = edtPassword.getText().toString();

                QBUser qbUser = new QBUser(user,password);
                QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        Toast.makeText(getBaseContext(),"Login successful", Toast.LENGTH_SHORT).show();


                        /*Intent passInfo = new Intent (MainActivity.this, Story.class);
                        passInfo.putExtra("user", user);
                        passInfo.putExtra("password", password);*/

                        Intent intent = new Intent (MainActivity.this, theStories.class);
                        intent.putExtra("user", user);
                        intent.putExtra("password", password);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(getBaseContext(),"" +e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }

    private void getListItemData() {
        DBHelper helper = new DBHelper(MainActivity.this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = helper.getMyStoriesInformations(db);
        String id, user, password;

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

    private void initializeFramework() {
        QBSettings.getInstance().init(getApplicationContext(),APP_ID,AUTH_KEY,AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
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
}
