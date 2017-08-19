package com.letstellastory.android.letstellastory;

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

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;

import java.util.ArrayList;

import static com.letstellastory.android.letstellastory.R.id.password_reset;

public class ForgotPassword extends AppCompatActivity {
Button btnReset, btnCancel;
    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setTitle("Reset your password");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        centerTitle();

        btnReset = (Button) findViewById(password_reset);
        btnCancel = (Button) findViewById(R.id.password_cancel);
        email = (EditText) findViewById(R.id.signup_email_link);


         btnReset.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 QBUsers.resetPassword(email.getText().toString()).performAsync(new QBEntityCallback<Void>() {
                     @Override
                     public void onSuccess(Void result, Bundle args) {
                         Toast.makeText(ForgotPassword.this, "Please check your email inbox or junk", Toast.LENGTH_LONG).show();
                         finish();
                     }

                     @Override
                     public void onError(QBResponseException errors) {
                         Toast.makeText(ForgotPassword.this, "Please enter a valid email", Toast.LENGTH_LONG).show();
                     }
                 });

             }
         });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
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
