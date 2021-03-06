package com.letstellastory.android.letstellastory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {
    Button btnSignup, btnCancel;
    EditText edtUser, edtPassword, edtFullName, edtEmail;


    @Override
    protected void onRestart() {
        super.onRestart();
        QBChatService.getInstance().setReconnectionAllowed(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        QBSettings.getInstance().setAccountKey("UYy6wj-dzPJ4ePBZdMJM");
        QBSettings.getInstance().init(getApplicationContext(),"60149","NnE9q3LKjvKz6-e","hcWYgEvZmpcn5s8");

        QBChatService.getInstance().setReconnectionAllowed(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("User Information");
        builder.setMessage("Please be advised that signing up with \"Let's tell a story\" means you will be required to create a user name and a password and also add your full name to your profile. None of the information you enter here will be shared or used anywhere else but in this app for logging in and recovering forgotten passwords. Thank you for understanding");


        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                // You don't have to do anything here if you just want it dismissed when clicked
            }
        });
        builder.show();

        QBSettings.getInstance().setAccountKey("UYy6wj-dzPJ4ePBZdMJM");
        QBSettings.getInstance().init(getApplicationContext(),"60149","NnE9q3LKjvKz6-e","hcWYgEvZmpcn5s8");

        QBChatService.setDefaultAutoSendPresenceInterval(600);
        QBChatService.getInstance().setReconnectionAllowed(true);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        registerSession();
        setTitle("Sign up");
        //centerTitle();



        btnSignup = (Button) findViewById(R.id.signup_btnSignUp);
        btnCancel = (Button) findViewById(R.id.signup_btnCancel);

        edtPassword = (EditText)findViewById(R.id.signup_editPassword);
        edtUser = (EditText)findViewById(R.id.signup_editLogin);
        edtFullName = (EditText)findViewById(R.id.signup_editFullName);
        //edtEmail = (EditText)findViewById(R.id.signup_editEmail);

        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String user = edtUser.getText().toString();
                String password = edtPassword.getText().toString();
                //String email = edtEmail.getText().toString();

                QBUser qbUser = new QBUser(user,password);

                qbUser.setFullName(edtFullName.getText().toString());
                //qbUser.setEmail(email);




                QBUsers.signUp(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_LONG).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    private void registerSession() {
        QBAuth.createSession().performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR", e.getMessage());
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
            }

            else {
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
